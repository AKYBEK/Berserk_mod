package net.akul.berserkmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensions {
    public static final ResourceKey<Level> THE_HAND_KEY = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation("berserkmod", "the_hand_of_the_god"));

    public static final ResourceKey<DimensionType> THE_HAND_TYPE_KEY = ResourceKey.create(
            Registries.DIMENSION_TYPE, new ResourceLocation("berserkmod", "the_hand_of_the_god"));
}
