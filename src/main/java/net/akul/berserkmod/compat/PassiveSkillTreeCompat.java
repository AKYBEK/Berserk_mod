package net.akul.berserkmod.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

/**
 * Simplified compatibility with Passive Skill Tree mod
 */
public class PassiveSkillTreeCompat {
    public static final String MOD_ID = "passiveskillstree";
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
    
    /**
     * Simple attempt to unlock skill tree
     */
    public static void unlockApostleTree(ServerPlayer serverPlayer) {
        if (!isLoaded() || serverPlayer == null) {
            return;
        }
        
        try {
            // Simple command without complex logic
            serverPlayer.server.getCommands().performPrefixedCommand(
                serverPlayer.server.createCommandSourceStack(),
                "skilltree grant_tree " + serverPlayer.getName().getString() + " berserkmod:apostle_tree"
            );
        } catch (Exception e) {
            // Ignore errors to prevent crashes
            System.out.println("Could not unlock skill tree: " + e.getMessage());
        }
    }
}