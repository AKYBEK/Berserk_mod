package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BehelitItem extends Item {
    // Простое хранение без сложных capability
    private static final Map<UUID, Long> playersInDimension = new HashMap<>();
    private static final Map<UUID, ServerLevel> playerOriginalDimensions = new HashMap<>();
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();
    private static final long DIMENSION_TIME = 5 * 60 * 20; // 5 минут в тиках
    private static final long COOLDOWN_TIME = 6000; // 5 минут кулдаун

    public BehelitItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return false;
        if (player.level().isClientSide) return false;

        // Простой эффект без сложной логики
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
        
        if (target instanceof Player targetPlayer) {
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        UUID playerId = serverPlayer.getUUID();
        long currentTime = level.getGameTime();
        
        // Проверка кулдауна
        if (playerCooldowns.containsKey(playerId)) {
            long lastUse = playerCooldowns.get(playerId);
            if (currentTime - lastUse < COOLDOWN_TIME) {
                serverPlayer.sendSystemMessage(Component.literal("The Behelit is not ready yet...").withStyle(ChatFormatting.DARK_RED));
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }
        
        // Простая телепортация без сложной логики
        ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
        if (targetLevel != null) {
            // Сохраняем данные
            playerOriginalDimensions.put(playerId, serverPlayer.serverLevel());
            playerCooldowns.put(playerId, currentTime);
            playersInDimension.put(playerId, currentTime);
            
            // Телепортируем
            serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, 
                serverPlayer.getYRot(), serverPlayer.getXRot());
            serverPlayer.sendSystemMessage(Component.literal("You entered the Hand of God").withStyle(ChatFormatting.DARK_RED));
            
            // Простые эффекты
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 6000, 1));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.SPEED, 6000, 1));
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        UUID playerId = serverPlayer.getUUID();
        
        // Проверяем, нужно ли вернуть игрока
        if (serverPlayer.level().dimension() == ModDimensions.THE_HAND_KEY) {
            if (playersInDimension.containsKey(playerId)) {
                long entryTime = playersInDimension.get(playerId);
                long currentTime = serverPlayer.level().getGameTime();
                
                if (currentTime - entryTime >= DIMENSION_TIME) {
                    // Возвращаем игрока
                    ServerLevel originalLevel = playerOriginalDimensions.get(playerId);
                    if (originalLevel == null) {
                        originalLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
                    }
                    
                    if (originalLevel != null) {
                        serverPlayer.teleportTo(originalLevel, 
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 
                            serverPlayer.getYRot(), serverPlayer.getXRot());
                        serverPlayer.sendSystemMessage(Component.literal("You returned from the Hand of God").withStyle(ChatFormatting.DARK_RED));
                    }
                    
                    // Очищаем данные
                    playersInDimension.remove(playerId);
                    playerOriginalDimensions.remove(playerId);
                }
            }
        } else {
            // Если игрок покинул измерение другим способом
            if (playersInDimension.containsKey(playerId)) {
                playersInDimension.remove(playerId);
                playerOriginalDimensions.remove(playerId);
            }
        }
    }
}