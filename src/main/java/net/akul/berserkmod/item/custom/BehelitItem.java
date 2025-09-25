package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();
    private static final long DIMENSION_TIME = 5 * 60 * 20; // 5 minutes in ticks
    private static final long COOLDOWN_TIME = 6000; // 5 minutes cooldown

    public BehelitItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        UUID playerId = serverPlayer.getUUID();
        long currentTime = level.getGameTime();
        
        // Check cooldown
        if (playerCooldowns.containsKey(playerId)) {
            long lastUse = playerCooldowns.get(playerId);
            if (currentTime - lastUse < COOLDOWN_TIME) {
                serverPlayer.sendSystemMessage(Component.literal("The Behelit is not ready yet...").withStyle(ChatFormatting.DARK_RED));
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }
        
        // Teleport to dimension
        ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
        if (targetLevel != null) {
            // Save data
            playerOriginalDimensions.put(playerId, serverPlayer.serverLevel());
            playerCooldowns.put(playerId, currentTime);
            playersInDimension.put(playerId, currentTime);
            
            // Teleport
            serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, 
                serverPlayer.getYRot(), serverPlayer.getXRot());
            serverPlayer.sendSystemMessage(Component.literal("You entered the Hand of God").withStyle(ChatFormatting.DARK_RED));
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        UUID playerId = serverPlayer.getUUID();
        
        // Check if player needs to be returned
        if (serverPlayer.level().dimension() == ModDimensions.THE_HAND_KEY) {
            if (playersInDimension.containsKey(playerId)) {
                long entryTime = playersInDimension.get(playerId);
                long currentTime = serverPlayer.level().getGameTime();
                
                if (currentTime - entryTime >= DIMENSION_TIME) {
                    // Return player
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
                    
                    // Clear data
                    playersInDimension.remove(playerId);
                    playerOriginalDimensions.remove(playerId);
                }
            }
        } else {
            // If player left dimension another way
            if (playersInDimension.containsKey(playerId)) {
                playersInDimension.remove(playerId);
                playerOriginalDimensions.remove(playerId);
            }
        }
    }
}