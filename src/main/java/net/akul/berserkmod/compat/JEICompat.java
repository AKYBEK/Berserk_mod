package net.akul.berserkmod.compat;

import net.akul.berserkmod.item.ModItems;
import net.minecraft.world.item.ItemStack;

/**
 * Совместимость с JEI (Just Enough Items)
 */
public class JEICompat {
    
    /**
     * Регистрирует информацию о предметах в JEI
     */
    public static void registerItemInfo() {
        if (!ModCompat.isJEILoaded()) return;
        
        try {
            // Здесь будет регистрация информации о Behelit в JEI
            // Например, описание, рецепты, использование
            registerBehelitInfo();
        } catch (Exception e) {
            System.err.println("Error registering JEI info: " + e.getMessage());
        }
    }
    
    private static void registerBehelitInfo() {
        // Информация о Behelit для JEI
        ItemStack behelit = new ItemStack(ModItems.BEHELIT.get());
        
        // Здесь будет добавление описания в JEI
        // jeiHelpers.getIngredientManager().addIngredientsAtRuntime(...)
    }
}