package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.data.PlayerBerserkData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BehelitItem extends Item {
    private static final Map<UUID, Long> playersInDimension = new HashMap<>();
    private static final Map<UUID, ServerLevel> playerOriginalDimensions = new HashMap<>();
    private static final long DIMENSION_TIME = 5 * 60 * 20; // 5 минут в тиках (20 тиков = 1 секунда)

    public BehelitItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return false;
        
        // Safety check to prevent crashes
        if (player.level() == null || player.getUUID() == null) {
            return false;
        }
        if (player.level().isClientSide) return false;

        // Добавляем эффект тьмы атакующему
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        
        // Если цель - игрок, добавляем эффект и ему
        if (target instanceof Player targetPlayer) {
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Safety checks to prevent crashes
            if (serverPlayer.getUUID() == null || serverPlayer.server == null) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            
            // Check if player already used Behelit recently (cooldown)
            long currentTime = level.getGameTime();
            long lastUse = PlayerBerserkData.getPlayerTransformTime(serverPlayer);
            if (currentTime - lastUse < 6000) { // 5 minute cooldown
                serverPlayer.sendSystemMessage(Component.literal("The Behelit is not ready yet...").withStyle(ChatFormatting.DARK_RED));
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            
            // Попытка телепортации в измерение "Рука Бога"
            ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
            if (targetLevel != null) {
                try {
                // Сохраняем текущее измерение игрока
                playerOriginalDimensions.put(serverPlayer.getUUID(), serverPlayer.serverLevel());
                
                    // Mark player as having used Behelit
                    PlayerBerserkData.markPlayerUsedBehelit(serverPlayer);
                    PlayerBerserkData.setPlayerTransformTime(serverPlayer, currentTime);
                    
                // Телепортируем в измерение
                    serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, 
                        serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.sendSystemMessage(Component.literal("Вы попали в длань Господа").withStyle(ChatFormatting.DARK_RED));
                
                // Запускаем таймер на 5 минут
                    playersInDimension.put(serverPlayer.getUUID(), currentTime);
                
                // Unlock Apostle skill tree if Passive Skill Tree mod is loaded
                unlockApostleSkillTree(serverPlayer);
                    
                } catch (Exception e) {
                    // Handle teleportation errors gracefully
                    serverPlayer.sendSystemMessage(Component.literal("Failed to enter the Hand of God").withStyle(ChatFormatting.RED));
                    System.err.println("Behelit teleportation error: " + e.getMessage());
                    return InteractionResultHolder.fail(player.getItemInHand(hand));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.literal("Измерение недоступно.").withStyle(ChatFormatting.DARK_RED));
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    /**
     * Unlocks the Apostle skill tree for the player
     */
    private void unlockApostleSkillTree(ServerPlayer player) {
        if (!ModList.get().isLoaded("passiveskillstree")) {
            return;
        }
        
        try {
            // Safety check
            if (player.server == null) {
                return;
            }
            
            // Try to unlock via command first
            player.server.getCommands().performPrefixedCommand(
                player.server.createCommandSourceStack(),
                "skilltree grant_tree " + player.getName().getString() + " berserkmod:apostle_tree"
            );
            
            player.sendSystemMessage(Component.literal("Вы получили силы Апостола!").withStyle(ChatFormatting.DARK_RED));
            
        } catch (Exception e) {
            // Fallback: try reflection-based approach (safer)
            try {
                unlockSkillTreeViaReflection(player);
            } catch (Exception ex) {
                System.err.println("Failed to unlock Apostle skill tree: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Fallback method using reflection to unlock skill tree
     */
    private void unlockSkillTreeViaReflection(ServerPlayer player) throws Exception {
        // Try to use SkillTreeApi if available
        if (player == null || player.getUUID() == null) {
            throw new Exception("Invalid player data");
        }
        
        // Safer reflection approach
        Class<?> skillTreeApiClass = Class.forName("net.impleri.passiveskillstree.api.SkillTreeApi");
        java.lang.reflect.Method unlockTreeMethod = skillTreeApiClass.getMethod("unlockTree", 
            net.minecraft.world.entity.player.Player.class, 
            net.minecraft.resources.ResourceLocation.class);
        
        net.minecraft.resources.ResourceLocation treeLocation = 
            new net.minecraft.resources.ResourceLocation("berserkmod", "apostle_tree");
        
        unlockTreeMethod.invoke(null, player, treeLocation);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;
        
        // Safety checks
        if (serverPlayer.getUUID() == null || serverPlayer.level() == null) {
            return;
        }

        UUID playerId = serverPlayer.getUUID();
        
        // Проверяем, находится ли игрок в измерении "Рука Бога"
        if (serverPlayer.level().dimension() == ModDimensions.THE_HAND_KEY) {
            if (playersInDimension.containsKey(playerId)) {
                long entryTime = playersInDimension.get(playerId);
                long currentTime = serverPlayer.level().getGameTime();
                
                // Если прошло 5 минут, возвращаем игрока
                if (currentTime - entryTime >= DIMENSION_TIME) {
                    try {
                    ServerLevel originalLevel = playerOriginalDimensions.get(playerId);
                    if (originalLevel == null) {
                        // Если не сохранили оригинальное измерение, возвращаем в обычный мир
                        originalLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
                    }
                    
                    if (originalLevel != null) {
                        serverPlayer.teleportTo(originalLevel, 
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 
                            serverPlayer.getYRot(), serverPlayer.getXRot());
                        serverPlayer.sendSystemMessage(Component.literal("Вы вернулись из длани Господа").withStyle(ChatFormatting.DARK_RED));
                    }
                    
                    // Убираем игрока из списков
                    playersInDimension.remove(playerId);
                    playerOriginalDimensions.remove(playerId);
                        
                    } catch (Exception e) {
                        System.err.println("Error returning player from Hand dimension: " + e.getMessage());
                        // Clean up anyway
                        playersInDimension.remove(playerId);
                        playerOriginalDimensions.remove(playerId);
                    }
                }
            }
        } else {
            // Если игрок покинул измерение другим способом, убираем его из списков
            if (playersInDimension.containsKey(playerId)) {
                playersInDimension.remove(playerId);
                playerOriginalDimensions.remove(playerId);
            }
        }
    }
}