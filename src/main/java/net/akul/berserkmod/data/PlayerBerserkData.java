package net.akul.berserkmod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simplified player data for tracking Behelit usage
 * Uses static storage to avoid capability sync issues
 */
public class PlayerBerserkData implements INBTSerializable<CompoundTag> {
    
    // Static storage to avoid capability sync issues during login
    private static final Map<UUID, Boolean> playersUsedBehelit = new HashMap<>();
    private static final Map<UUID, Long> playerTransformTimes = new HashMap<>();
    
    private boolean hasUsedBehelit = false;
    private long lastTransformTime = 0;
    
    public boolean hasUsedBehelit() {
        return hasUsedBehelit;
    }
    
    public void setUsedBehelit(boolean used) {
        this.hasUsedBehelit = used;
    }
    
    public long getLastTransformTime() {
        return lastTransformTime;
    }
    
    public void setLastTransformTime(long time) {
        this.lastTransformTime = time;
    }
    
    // Static methods for safe data access
    public static void markPlayerUsedBehelit(Player player) {
        if (player != null && player.getUUID() != null) {
            playersUsedBehelit.put(player.getUUID(), true);
        }
    }
    
    public static boolean hasPlayerUsedBehelit(Player player) {
        if (player == null || player.getUUID() == null) {
            return false;
        }
        return playersUsedBehelit.getOrDefault(player.getUUID(), false);
    }
    
    public static void clearPlayerBehelitStatus(Player player) {
        if (player != null && player.getUUID() != null) {
            playersUsedBehelit.remove(player.getUUID());
            playerTransformTimes.remove(player.getUUID());
        }
    }
    
    public static void setPlayerTransformTime(Player player, long time) {
        if (player != null && player.getUUID() != null) {
            playerTransformTimes.put(player.getUUID(), time);
        }
    }
    
    public static long getPlayerTransformTime(Player player) {
        if (player == null || player.getUUID() == null) {
            return 0;
        }
        return playerTransformTimes.getOrDefault(player.getUUID(), 0L);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasUsedBehelit", hasUsedBehelit);
        tag.putLong("lastTransformTime", lastTransformTime);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt != null) {
            hasUsedBehelit = nbt.getBoolean("hasUsedBehelit");
            lastTransformTime = nbt.getLong("lastTransformTime");
        }
    }
}