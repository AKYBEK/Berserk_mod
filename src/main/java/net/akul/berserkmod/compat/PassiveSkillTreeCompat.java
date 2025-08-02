package net.akul.berserkmod.compat;

import net.akul.berserkmod.ModDimensions;
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
 * Совместимость с модом Passive Skill Tree
 */
@Mod.EventBusSubscriber(modid = "berserkmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
            System.out.println("[BerserkMod] Adding " + points + " skill points to " + player.getName().getString());
        } catch (Exception e) {
            System.err.println("[BerserkMod] Error integrating with Passive Skill Tree: " + e.getMessage());
        }
    }
    
    /**
     * Перехватывает открытие меню Passive Skill Tree и БЛОКИРУЕТ его для игроков, использовавших Behelit
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!isLoaded()) return;
        
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        // Проверяем, что это экран Passive Skill Tree
        String screenClassName = event.getScreen().getClass().getName();
        boolean isPassiveSkillScreen = screenClassName.contains("passiveskillstree") || 
                                     screenClassName.contains("SkillTree") || 
                                     screenClassName.contains("PassiveSkill") ||
                                     screenClassName.contains("SkillScreen");
        
        if (!isPassiveSkillScreen) {
            return;
        }
        
        System.out.println("[BerserkMod] Detected Passive Skill Tree screen: " + screenClassName);
        
        // Проверяем условия для блокировки меню:
        // 1. Игрок использовал Behelit
        // 2. Игрок НЕ находится в измерении "Рука Бога"
        boolean hasUsedBehelit = PlayerBerserkData.hasPlayerUsedBehelit(player);
        boolean inHandDimension = player.level().dimension() == ModDimensions.THE_HAND_KEY;
        
        System.out.println("[BerserkMod] Has used Behelit: " + hasUsedBehelit + ", In Hand dimension: " + inHandDimension);
        
        if (hasUsedBehelit && !inHandDimension) {
            System.out.println("[BerserkMod] BLOCKING Passive Skill Tree screen - player used Behelit!");
            
            // ПОЛНОСТЬЮ БЛОКИРУЕМ открытие меню Passive Skill Tree
            event.setCanceled(true);
            
            // Показываем сообщение игроку
            if (player != null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§4[Berserk] Your soul has been marked by the Behelit. The old ways of power are closed to you."));
            }
        } else {
            System.out.println("[BerserkMod] Allowing normal Passive Skill Tree screen");
        }
    }
}