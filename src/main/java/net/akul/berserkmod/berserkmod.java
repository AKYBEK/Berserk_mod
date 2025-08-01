package net.akul.berserkmod;

import com.mojang.logging.LogUtils;
import net.akul.berserkmod.item.ModItems;
import net.akul.berserkmod.item.ModCreativeModeTabs;
import net.akul.berserkmod.compat.ModCompat;
import net.akul.berserkmod.compat.JEICompat;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(berserkmod.MOD_ID)
public class berserkmod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "berserkmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public berserkmod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register mod items
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        
        // Проверяем совместимость с модами
        checkModCompatibility();

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }
    
    private void checkModCompatibility() {
        LOGGER.info("Checking mod compatibility...");
        
        if (ModCompat.isPassiveSkillTreeLoaded()) {
            LOGGER.info("Passive Skill Tree detected - enabling compatibility");
        }
        
        if (ModCompat.isJEILoaded()) {
            LOGGER.info("JEI detected - enabling compatibility");
            JEICompat.registerItemInfo();
        }
        
        if (ModCompat.isCuriosLoaded()) {
            LOGGER.info("Curios detected - enabling compatibility");
        }
        
        if (ModCompat.isPlayerExLoaded()) {
            LOGGER.info("PlayerEx detected - enabling compatibility");
        }
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        // Этот метод больше не нужен, так как мы используем собственную вкладку
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            
        }
        
        @SubscribeEvent
        public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(new ResourceLocation("berserkmod", "the_hand_of_the_god"), new ModDimensionEffects());
        }
    }
}