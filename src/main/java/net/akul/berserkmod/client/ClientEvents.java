package net.akul.berserkmod.client;

import net.akul.berserkmod.client.gui.CharacterScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "berserkmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.OPEN_CHARACTER_MENU_KEY);
    }
}

@Mod.EventBusSubscriber(modid = "berserkmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
class ClientForgeEvents {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.OPEN_CHARACTER_MENU_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new CharacterScreen());
        }
    }
}