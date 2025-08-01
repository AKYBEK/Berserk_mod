package net.akul.berserkmod.events;

import net.akul.berserkmod.compat.ModCompat;
import net.akul.berserkmod.compat.PassiveSkillTreeCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * События для интеграции с другими модами
 */
@Mod.EventBusSubscriber(modid = "berserkmod")
public class CompatibilityEvents {
    
    /**
     * Событие при входе игрока в мир
     * Можно использовать для инициализации совместимости
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        
        // Проверяем совместимость с модами
        if (ModCompat.isPassiveSkillTreeLoaded()) {
            // Инициализация для Passive Skill Tree
            initPassiveSkillTreeCompat(player);
        }
        
        if (ModCompat.isPlayerExLoaded()) {
            // Инициализация для PlayerEx
            initPlayerExCompat(player);
        }
    }
    
    private static void initPassiveSkillTreeCompat(Player player) {
        // Логика инициализации для Passive Skill Tree
        System.out.println("Initializing Passive Skill Tree compatibility for " + player.getName().getString());
    }
    
    private static void initPlayerExCompat(Player player) {
        // Логика инициализации для PlayerEx
        System.out.println("Initializing PlayerEx compatibility for " + player.getName().getString());
    }
}