package net.akul.berserkmod.advancement;

import net.akul.berserkmod.berserkmod;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = berserkmod.MOD_ID)
public class ModAdvancements {
    
    public static void grantBehelitAdvancement(net.minecraft.server.level.ServerPlayer player) {
        if (player.getServer() != null) {
            Advancement advancement = player.getServer().getAdvancements()
                .getAdvancement(new ResourceLocation(berserkmod.MOD_ID, "Angel ? Or Demon ?"));
            
            if (advancement != null) {
                net.minecraft.server.PlayerAdvancements playerAdvancements = player.getAdvancements();
                if (!playerAdvancements.getOrStartProgress(advancement).isDone()) {
                    // Grant all criteria for this advancement
                    for (String criterion : advancement.getCriteria().keySet()) {
                        playerAdvancements.award(advancement, criterion);
                    }
                }
            }
        }
    }
}