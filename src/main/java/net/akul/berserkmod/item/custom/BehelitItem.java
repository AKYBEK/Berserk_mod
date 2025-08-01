package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.data.PlayerBerserkData;
import net.akul.berserkmod.compat.ModCompat;
import net.akul.berserkmod.compat.PassiveSkillTreeCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    private static final Map<UUID, Long> playersInDimension = new HashMap<>();
    private static final Map<UUID, ServerLevel> playerOriginalDimensions = new HashMap<>();
    private static final long DIMENSION_TIME = 5 * 60 * 20; // 5 минут в тиках (20 тиков = 1 секунда)

    public BehelitItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Добавляем эффект тьмы на 5 секунд
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
            
            // Отправляем сообщение игроку
            serverPlayer.sendSystemMessage(Component.literal("Behelit активирован! Тьма окутывает вас...").withStyle(ChatFormatting.DARK_RED));
            
            // Отмечаем, что игрок использовал Behelit
            PlayerBerserkData.markPlayerUsedBehelit(serverPlayer);
            
            // Телепортируем игрока в измерение "Рука Бога"
            ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
            if (targetLevel != null) {
                // Сохраняем текущее измерение игрока
                playerOriginalDimensions.put(serverPlayer.getUUID(), serverPlayer.serverLevel());
                
                // Телепортируем игрока
                serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.sendSystemMessage(Component.literal("Вы попали в длань Господа").withStyle(ChatFormatting.DARK_RED));
                
                // Запускаем таймер на 5 минут
                long currentTime = level.getGameTime();
                playersInDimension.put(serverPlayer.getUUID(), currentTime);
            }
            
            // Уничтожаем Behelit
            itemStack.shrink(1);
            
            // Интеграция с другими модами
            handleModIntegration(serverPlayer);
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
    
    /**
     * Обработка интеграции с другими модами при активации Behelit
     */
    private void handleModIntegration(ServerPlayer player) {
        // Интеграция с Passive Skill Tree
        if (ModCompat.isPassiveSkillTreeLoaded()) {
            // Даем очки навыков за активацию Behelit
            PassiveSkillTreeCompat.addSkillPoints(player, 5);
        }
        
        // Можно добавить интеграцию с другими модами
        if (ModCompat.isPlayerExLoaded()) {
            // Интеграция с PlayerEx (система атрибутов)
            handlePlayerExIntegration(player);
        }
    }
    
    private void handlePlayerExIntegration(ServerPlayer player) {
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