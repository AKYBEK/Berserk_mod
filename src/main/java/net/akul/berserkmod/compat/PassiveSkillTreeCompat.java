package net.akul.berserkmod.compat;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.data.PlayerBerserkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Compatibility with Passive Skill Tree mod
 */
@Mod.EventBusSubscriber(modid = "berserkmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PassiveSkillTreeCompat {
    private static final String MOD_ID = "passiveskillstree";
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
    
    /**
     * Modifies Passive Skill Tree menu to hide skill buttons for players who used Behelit
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!isLoaded()) return;
        
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        Screen screen = event.getScreen();
        String screenClassName = screen.getClass().getName().toLowerCase();
        
        // Check if this is a Passive Skill Tree screen
        boolean isPassiveSkillScreen = screenClassName.contains("passiveskillstree") ||
                                     screenClassName.contains("skill") ||
                                     screenClassName.contains("passive");
        
        if (!isPassiveSkillScreen) {
            return;
        }
        
        System.out.println("Detected Passive Skill Tree screen: " + screen.getClass().getName());
        
        // Check conditions for hiding skill buttons:
        // 1. Player used Behelit
        // 2. Player is NOT in "Hand of God" dimension
        boolean hasUsedBehelit = PlayerBerserkData.hasPlayerUsedBehelit(player);
        boolean inHandDimension = player.level().dimension() == ModDimensions.THE_HAND_KEY;
        
        System.out.println("Has used Behelit: " + hasUsedBehelit + ", In Hand dimension: " + inHandDimension);
        
        if (hasUsedBehelit && !inHandDimension) {
            System.out.println("Hiding skill buttons from Passive Skill Tree screen");
            hideSkillButtons(screen);
        }
    }
    
    /**
     * Hides skill buttons from the Passive Skill Tree screen using reflection
     */
    private static void hideSkillButtons(Screen screen) {
        try {
            // Get all fields from the screen class
            Field[] fields = screen.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(screen);
                
                // Look for button lists or widget lists
                if (fieldValue instanceof List<?> list) {
                    // Check if it's a list of widgets/buttons
                    if (!list.isEmpty() && isButtonOrWidget(list.get(0))) {
                        System.out.println("Found button/widget list: " + field.getName() + " with " + list.size() + " items");
                        
                        // Clear the list to hide all buttons
                        list.clear();
                        System.out.println("Cleared button/widget list: " + field.getName());
                    }
                }
                
                // Also check for individual button fields
                if (isButtonOrWidget(fieldValue)) {
                    System.out.println("Found individual button/widget: " + field.getName());
                    // Set button to null or make it invisible
                    field.set(screen, null);
                }
            }
            
            // Also try to clear the screen's renderables and children
            try {
                Field renderablesField = Screen.class.getDeclaredField("renderables");
                renderablesField.setAccessible(true);
                List<?> renderables = (List<?>) renderablesField.get(screen);
                renderables.clear();
                System.out.println("Cleared renderables list");
            } catch (Exception e) {
                System.out.println("Could not clear renderables: " + e.getMessage());
            }
            
            try {
                Field childrenField = Screen.class.getDeclaredField("children");
                childrenField.setAccessible(true);
                List<?> children = (List<?>) childrenField.get(screen);
                children.clear();
                System.out.println("Cleared children list");
            } catch (Exception e) {
                System.out.println("Could not clear children: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error hiding skill buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if an object is a button or widget
     */
    private static boolean isButtonOrWidget(Object obj) {
        if (obj == null) return false;
        
        String className = obj.getClass().getName().toLowerCase();
        return className.contains("button") || 
               className.contains("widget") || 
               className.contains("clickable") ||
               className.contains("skill");
    }
}