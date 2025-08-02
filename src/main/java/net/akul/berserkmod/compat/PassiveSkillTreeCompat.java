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
            // Пока что просто логируем
            System.out.println("Adding " + points + " skill points to " + player.getName().getString());
        } catch (Exception e) {
            System.err.println("Error integrating with Passive Skill Tree: " + e.getMessage());
        }
    }
    
    /**
     * Проверяет, есть ли у игрока определенный навык
     */
    public static boolean hasSkill(Player player, String skillName) {
        if (!isLoaded()) return false;
        
        try {
            // Здесь будет проверка навыка через API
            // return SkillTreeAPI.hasSkill(player, skillName);
            return false; // Временно
        } catch (Exception e) {
            System.err.println("Error checking skill: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Перехватывает открытие меню Passive Skill Tree и заменяет его на наше
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!isLoaded()) return;
        
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        // Проверяем, что это экран Passive Skill Tree (более точная проверка)
        String screenClassName = event.getScreen().getClass().getName();
        if (!screenClassName.contains("passiveskillstree") && 
            !screenClassName.contains("SkillTree") && 
            !screenClassName.contains("PassiveSkill")) {
            return;
        }
        
        System.out.println("Detected Passive Skill Tree screen: " + screenClassName);
        
        // Проверяем условия для замены меню:
        // 1. Игрок использовал Behelit
        // 2. Игрок НЕ находится в измерении "Рука Бога"
        boolean hasUsedBehelit = PlayerBerserkData.hasPlayerUsedBehelit(player);
        boolean inHandDimension = player.level().dimension() == ModDimensions.THE_HAND_KEY;
        
        System.out.println("Has used Behelit: " + hasUsedBehelit + ", In Hand dimension: " + inHandDimension);
        
        if (hasUsedBehelit && !inHandDimension) {
            System.out.println("Replacing Passive Skill Tree screen with Berserk Skills screen");
            // Отменяем открытие оригинального экрана
            event.setCanceled(true);
            
            // Открываем наш экран навыков Берсерка
            Minecraft.getInstance().setScreen(new BerserkSkillScreen());
        } else {
            System.out.println("Conditions not met for screen replacement");
        }
    }
}