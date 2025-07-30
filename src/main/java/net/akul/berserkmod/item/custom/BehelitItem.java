package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.compat.ModCompat;
import net.akul.berserkmod.compat.PassiveSkillTreeCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

    @SubscribeEvent
    public void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getItemInHand(event.getHand());
        
        // Проверяем, что игрок держит Behelit
        if (itemStack.getItem() != this) return;
        
        // Проверяем, что цель - другой игрок
        if (!(event.getTarget() instanceof Player targetPlayer)) return;
        
        // Проверяем, что это не тот же игрок
        if (player.getUUID().equals(targetPlayer.getUUID())) return;
        
        // Проверяем, что мы на сервере
        if (player.level().isClientSide) return;
        
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerPlayer serverTarget = (ServerPlayer) targetPlayer;
        
        // Добавляем эффект тьмы обоим игрокам на 5 секунд
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        serverTarget.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        
        // Отправляем сообщения игрокам
        serverPlayer.sendSystemMessage(Component.literal("Behelit активирован! Тьма окутывает вас...").withStyle(ChatFormatting.DARK_RED));
        serverTarget.sendSystemMessage(Component.literal("На вас воздействует сила Behelit!").withStyle(ChatFormatting.DARK_RED));
        
        // Телепортируем обоих игроков в измерение "Рука Бога"
        ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
        if (targetLevel != null) {
            // Сохраняем текущие измерения игроков
            playerOriginalDimensions.put(serverPlayer.getUUID(), serverPlayer.serverLevel());
            playerOriginalDimensions.put(serverTarget.getUUID(), serverTarget.serverLevel());
            
            // Телепортируем обоих игроков
            serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
            serverTarget.teleportTo(targetLevel, 3.5, 70, 3.5, serverTarget.getYRot(), serverTarget.getXRot());
            
            serverPlayer.sendSystemMessage(Component.literal("Вы попали в длань Господа").withStyle(ChatFormatting.DARK_RED));
            serverTarget.sendSystemMessage(Component.literal("Вы попали в длань Господа").withStyle(ChatFormatting.DARK_RED));
            
            // Запускаем таймер на 5 минут для обоих игроков
            long currentTime = player.level().getGameTime();
            playersInDimension.put(serverPlayer.getUUID(), currentTime);
            playersInDimension.put(serverTarget.getUUID(), currentTime);
        }
        
        // Уничтожаем Behelit
        itemStack.shrink(1);
        
        // Интеграция с другими модами
        handleModIntegration(serverPlayer, serverTarget);
        
        // Отменяем дальнейшую обработку события
        event.setCanceled(true);
    }
    
    /**
     * Обработка интеграции с другими модами при активации Behelit
     */
    private void handleModIntegration(ServerPlayer activator, ServerPlayer target) {
        // Интеграция с Passive Skill Tree
        if (ModCompat.isPassiveSkillTreeLoaded()) {
            // Даем очки навыков за активацию Behelit
            PassiveSkillTreeCompat.addSkillPoints(activator, 5);
            PassiveSkillTreeCompat.addSkillPoints(target, 3);
        }
        
        // Можно добавить интеграцию с другими модами
        if (ModCompat.isPlayerExLoaded()) {
            // Интеграция с PlayerEx (система атрибутов)
            handlePlayerExIntegration(activator, target);
        }
    }
    
    private void handlePlayerExIntegration(ServerPlayer activator, ServerPlayer target) {
        // Здесь будет логика для PlayerEx
        // Например, изменение атрибутов игроков
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        UUID playerId = serverPlayer.getUUID();
        
        // Проверяем, находится ли игрок в измерении "Рука Бога"
        if (serverPlayer.level().dimension() == ModDimensions.THE_HAND_KEY) {
            if (playersInDimension.containsKey(playerId)) {
                long entryTime = playersInDimension.get(playerId);
                long currentTime = serverPlayer.level().getGameTime();
                
                // Если прошло 5 минут, возвращаем игрока
                if (currentTime - entryTime >= DIMENSION_TIME) {
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