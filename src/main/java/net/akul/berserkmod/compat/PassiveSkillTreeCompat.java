package net.akul.berserkmod.compat;

import net.akul.berserkmod.data.PlayerBerserkData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Compatibility with Passive Skill Tree mod
 */
public class PassiveSkillTreeCompat {
    public static final String MOD_ID = "passiveskillstree";
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
    
    /**
     * Unlocks the Apostle skill tree for a player who used Behelit
     */
    public static void unlockApostleTree(Player player) {
        if (!isLoaded() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        // Safety checks to prevent crashes
        if (serverPlayer.getUUID() == null || serverPlayer.server == null) {
            System.err.println("Cannot unlock skill tree: Invalid player data");
            return;
        }
        
        try {
            // Mark player as having used Behelit
            PlayerBerserkData.markPlayerUsedBehelit(player);
            
            // Try command-based unlock first
            serverPlayer.server.getCommands().performPrefixedCommand(
                serverPlayer.server.createCommandSourceStack(),
                "skilltree grant_tree " + player.getName().getString() + " berserkmod:apostle_tree"
            );
            
            System.out.println("Successfully unlocked Apostle skill tree for " + player.getName().getString());
            
        } catch (Exception e) {
            // Fallback to reflection-based approach (safer)
            try {
                unlockTreeViaReflection(serverPlayer);
            } catch (Exception ex) {
                System.err.println("Failed to unlock Apostle skill tree: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Fallback method using reflection
     */
    private static void unlockTreeViaReflection(ServerPlayer player) throws Exception {
        // Safety check
        if (player == null || player.getUUID() == null) {
            throw new Exception("Invalid player data for skill tree unlock");
        }
        
        Class<?> skillTreeApiClass = Class.forName("net.impleri.passiveskillstree.api.SkillTreeApi");
        java.lang.reflect.Method unlockTreeMethod = skillTreeApiClass.getMethod("unlockTree", 
            Player.class, ResourceLocation.class);
        
        ResourceLocation treeLocation = new ResourceLocation("berserkmod", "apostle_tree");
        unlockTreeMethod.invoke(null, player, treeLocation);
        
        System.out.println("Unlocked Apostle tree via reflection for " + player.getName().getString());
    }
    
    /**
     * Checks if player has access to Apostle skills
     */
    public static boolean hasApostleAccess(Player player) {
        return PlayerBerserkData.hasPlayerUsedBehelit(player);
    }
}