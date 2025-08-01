package net.akul.berserkmod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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
 * Данные игрока для отслеживания использования Behelit
 */
public class PlayerBerserkData implements INBTSerializable<CompoundTag> {
    public static final Capability<PlayerBerserkData> PLAYER_BERSERK_DATA = 
            CapabilityManager.get(new CapabilityToken<PlayerBerserkData>() {});
    
    // Статическое хранилище для отслеживания игроков, использовавших Behelit
    private static final Map<UUID, Boolean> playersUsedBehelit = new HashMap<>();
    
    private boolean hasUsedBehelit = false;
    
    public boolean hasUsedBehelit() {
        return hasUsedBehelit;
    }
    
    public void setUsedBehelit(boolean used) {
        this.hasUsedBehelit = used;
    }
    
    // Статические методы для удобства
    public static void markPlayerUsedBehelit(Player player) {
        playersUsedBehelit.put(player.getUUID(), true);
    }
    
    public static boolean hasPlayerUsedBehelit(Player player) {
        return playersUsedBehelit.getOrDefault(player.getUUID(), false);
    }
    
    public static void clearPlayerBehelitStatus(Player player) {
        playersUsedBehelit.remove(player.getUUID());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasUsedBehelit", hasUsedBehelit);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        hasUsedBehelit = nbt.getBoolean("hasUsedBehelit");
    }
    
    @Mod.EventBusSubscriber(modid = "berserkmod", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CapabilityEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(PlayerBerserkData.class);
        }
    }
}