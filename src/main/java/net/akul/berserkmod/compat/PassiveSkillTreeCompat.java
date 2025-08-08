package net.akul.berserkmod.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * Упрощенная совместимость с Passive Skill Tree mod
 */
public class PassiveSkillTreeCompat {
    public static final String MOD_ID = "passiveskillstree";
    
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
    
    /**
     * Простая попытка разблокировать дерево навыков
     */
    public static void unlockApostleTree(Player player) {
        if (!isLoaded() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        try {
            // Простая команда без сложной логики
            serverPlayer.server.getCommands().performPrefixedCommand(
                serverPlayer.server.createCommandSourceStack(),
                "skilltree grant_tree " + player.getName().getString() + " berserkmod:apostle_tree"
            );
        } catch (Exception e) {
            // Игнорируем ошибки, чтобы не крашить игру
            System.out.println("Could not unlock skill tree: " + e.getMessage());
        }
    }
}