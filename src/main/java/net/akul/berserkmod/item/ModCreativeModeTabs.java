package net.akul.berserkmod.item;

import net.akul.berserkmod.berserkmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, berserkmod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BERSERK_TAB = CREATIVE_MODE_TABS.register("berserk_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BEHELIT.get()))
                    .title(Component.translatable("creativetab.berserk_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.BEHELIT.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}