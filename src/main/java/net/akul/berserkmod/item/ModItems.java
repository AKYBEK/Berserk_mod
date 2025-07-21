package net.akul.berserkmod.item;

import net.akul.berserkmod.berserkmod;
import net.akul.berserkmod.item.custom.BehelitItem; // <-- ВАЖНО: импорт BehelitItem

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, berserkmod.MOD_ID);

    public static final RegistryObject<Item> BEHELIT = ITEMS.register("behelit",
            () -> new BehelitItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
