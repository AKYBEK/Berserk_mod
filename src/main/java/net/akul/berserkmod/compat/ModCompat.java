package net.akul.berserkmod.compat;

import net.minecraftforge.fml.ModList;

/**
 * Класс для проверки совместимости с другими модами
 */
public class ModCompat {
    // ID модов для проверки
    public static final String PASSIVE_SKILL_TREE_ID = "passiveskillstree";
    public static final String JEI_ID = "jei";
    public static final String CURIOS_ID = "curios";
    public static final String PLAYER_EX_ID = "playerex";
    
    // Проверка загружен ли мод
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    
    // Проверки для конкретных модов
    public static boolean isPassiveSkillTreeLoaded() {
        return isModLoaded(PASSIVE_SKILL_TREE_ID);
    }
    
    public static boolean isJEILoaded() {
        return isModLoaded(JEI_ID);
    }
    
    public static boolean isCuriosLoaded() {
        return isModLoaded(CURIOS_ID);
    }
    
    public static boolean isPlayerExLoaded() {
        return isModLoaded(PLAYER_EX_ID);
    }
}