package net.akul.berserkmod.compat;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.data.PlayerBerserkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
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
import java.util.ArrayList;

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
     * Removes skill upgrade buttons from Passive Skill Tree menu for players who used Behelit
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!isLoaded()) return;
        
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        Screen screen = event.getScreen();
        String screenClassName = screen.getClass().getName();
        
        // Check if this is a Passive Skill Tree screen
        boolean isPassiveSkillScreen = screenClassName.contains("passiveskillstree") ||
                                     screenClassName.contains("PassiveSkill") ||
                                     screenClassName.contains("SkillTree") ||
                                     screenClassName.contains("skill");
        
        if (!isPassiveSkillScreen) {
            return;
        }
        
        System.out.println("Detected Passive Skill Tree screen: " + screenClassName);
        
        // Check conditions for hiding skill buttons:
        // 1. Player used Behelit
        // 2. Player is NOT in "Hand of God" dimension
        boolean hasUsedBehelit = PlayerBerserkData.hasPlayerUsedBehelit(player);
        boolean inHandDimension = player.level().dimension() == ModDimensions.THE_HAND_KEY;
        
        System.out.println("Has used Behelit: " + hasUsedBehelit + ", In Hand dimension: " + inHandDimension);
        
        if (hasUsedBehelit && !inHandDimension) {
            System.out.println("Removing skill upgrade buttons from Passive Skill Tree screen");
            removeSkillButtons(screen);
        }
    }
    
    /**
     * Removes skill upgrade buttons from the Passive Skill Tree screen using reflection
     */
    private static void removeSkillButtons(Screen screen) {
        try {
            // Get all fields from the screen class and its superclasses
            Class<?> currentClass = screen.getClass();
            while (currentClass != null) {
                Field[] fields = currentClass.getDeclaredFields();
                
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(screen);
                    
                    // Look for button lists or widget lists
                    if (fieldValue instanceof List<?> list) {
                        processButtonList(list, field.getName());
                    }
                    
                    // Also check for individual button fields
                    if (isSkillButton(fieldValue)) {
                        System.out.println("Found individual skill button: " + field.getName());
                        // Make button invisible instead of removing it to avoid crashes
                        if (fieldValue instanceof AbstractWidget widget) {
                            widget.visible = false;
                            widget.active = false;
                        }
                    }
                }
                
                currentClass = currentClass.getSuperclass();
            }
            
            // Process the screen's children and renderables lists
            processScreenWidgets(screen);
            
        } catch (Exception e) {
            System.err.println("Error removing skill buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Process a list that might contain buttons
     */
    private static void processButtonList(List<?> list, String fieldName) {
        if (list.isEmpty()) return;
        
        // Check if it's a list of widgets/buttons
        Object firstItem = list.get(0);
        if (isButtonOrWidget(firstItem)) {
            System.out.println("Found button/widget list: " + fieldName + " with " + list.size() + " items");
            
            // Create a new list with only non-skill buttons
            List<Object> filteredList = new ArrayList<>();
            for (Object item : list) {
                if (!isSkillButton(item)) {
                    filteredList.add(item);
                } else {
                    System.out.println("Removing skill button from list: " + item.getClass().getSimpleName());
                    // Make button invisible instead of removing to avoid crashes
                    if (item instanceof AbstractWidget widget) {
                        widget.visible = false;
                        widget.active = false;
                    }
                }
            }
            
            // Don't clear the entire list, just hide skill buttons
            System.out.println("Processed button list: " + fieldName);
        }
    }
    
    /**
     * Process the screen's main widget lists
     */
    private static void processScreenWidgets(Screen screen) {
        try {
            // Process renderables
            Field renderablesField = Screen.class.getDeclaredField("renderables");
            renderablesField.setAccessible(true);
            List<?> renderables = (List<?>) renderablesField.get(screen);
            
            for (Object renderable : new ArrayList<>(renderables)) {
                if (isSkillButton(renderable)) {
                    if (renderable instanceof AbstractWidget widget) {
                        widget.visible = false;
                        widget.active = false;
                    }
                }
            }
            
            // Process children (clickable elements)
            Field childrenField = Screen.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            List<?> children = (List<?>) childrenField.get(screen);
            
            for (Object child : new ArrayList<>(children)) {
                if (isSkillButton(child)) {
                    if (child instanceof AbstractWidget widget) {
                        widget.visible = false;
                        widget.active = false;
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Could not process screen widgets: " + e.getMessage());
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
               obj instanceof AbstractWidget ||
               obj instanceof Button;
    }
    
    /**
     * Checks if an object is specifically a skill upgrade button
     */
    private static boolean isSkillButton(Object obj) {
        if (obj == null) return false;
        
        String className = obj.getClass().getName().toLowerCase();
        
        // Look for skill-related button classes
        boolean isSkillRelated = className.contains("skill") ||
                               className.contains("upgrade") ||
                               className.contains("passive") ||
                               className.contains("tree") ||
                               className.contains("node");
        
        // Make sure it's actually a button/widget
        boolean isButton = isButtonOrWidget(obj);
        
        return isSkillRelated && isButton;
    }
}