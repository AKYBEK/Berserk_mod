package net.akul.berserkmod.compat;

import net.akul.berserkmod.ModDimensions;
import net.akul.berserkmod.client.gui.BerserkSkillScreen;
import net.akul.berserkmod.data.PlayerBerserkData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;

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
     * Intercepts Passive Skill Tree menu opening and replaces it with our Berserk menu
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!isLoaded()) return;
        
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        // Check if this is a Passive Skill Tree screen
        String screenClassName = event.getScreen().getClass().getSimpleName().toLowerCase();
        String fullClassName = event.getScreen().getClass().getName().toLowerCase();
        
        System.out.println("Screen opening: " + event.getScreen().getClass().getName());
        
        // More comprehensive check for Passive Skill Tree screens
        boolean isPassiveSkillScreen = screenClassName.contains("skill") || 
                                     screenClassName.contains("passive") ||
                                     screenClassName.contains("tree") ||
                                     fullClassName.contains("passiveskillstree") ||
                                     fullClassName.contains("skill") ||
                                     fullClassName.contains("passive");
        
        if (!isPassiveSkillScreen) {
            return;
        }
        
        System.out.println("Detected Passive Skill Tree screen: " + event.getScreen().getClass().getName());
        
        // Check conditions for menu replacement:
        // 1. Player used Behelit
        // 2. Player is NOT in "Hand of God" dimension
        boolean hasUsedBehelit = PlayerBerserkData.hasPlayerUsedBehelit(player);
        boolean inHandDimension = player.level().dimension() == ModDimensions.THE_HAND_KEY;
        
        System.out.println("Has used Behelit: " + hasUsedBehelit + ", In Hand dimension: " + inHandDimension);
        
        if (hasUsedBehelit && !inHandDimension) {
            System.out.println("Replacing Passive Skill Tree screen with Berserk Skills screen");
            // Cancel the original screen opening
            event.setCanceled(true);
            
            // Open our Berserk skills screen instead
            Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().setScreen(new BerserkSkillScreen());
            });
        } else {
            System.out.println("Conditions not met for screen replacement");
        }
    }
}