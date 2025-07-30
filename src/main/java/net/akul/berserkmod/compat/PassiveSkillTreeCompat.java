package net.akul.berserkmod.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Совместимость с модом Passive Skill Tree
 */
public class PassiveSkillTreeCompat {
    private static final String MOD_ID = "passiveskillstree";
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
    
    /**
     * Добавляет очки навыков игроку (если мод загружен)
     */
    public static void addSkillPoints(Player player, int points) {
        if (!isLoaded()) return;
        
        try {
            // Здесь будет интеграция с API Passive Skill Tree
            // Пример: SkillTreeAPI.addSkillPoints(player, points);
            // Пока что просто логируем
            System.out.println("Adding " + points + " skill points to " + player.getName().getString());
        } catch (Exception e) {
            System.err.println("Error integrating with Passive Skill Tree: " + e.getMessage());
        }
    }
    
    /**
     * Проверяет, есть ли у игрока определенный навык
     */
    public static boolean hasSkill(Player player, String skillName) {
        if (!isLoaded()) return false;
        
        try {
            // Здесь будет проверка навыка через API
            // return SkillTreeAPI.hasSkill(player, skillName);
            return false; // Временно
        } catch (Exception e) {
            System.err.println("Error checking skill: " + e.getMessage());
            return false;
        }
    }
}