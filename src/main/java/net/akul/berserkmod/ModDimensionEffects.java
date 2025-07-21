package net.akul.berserkmod;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModDimensionEffects extends DimensionSpecialEffects {
    
    public ModDimensionEffects() {
        super(Float.NaN, true, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        // Красный туман
        return new Vec3(0.545, 0.0, 0.0); // #8B0000 в RGB
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public float getCloudHeight() {
        return Float.NaN;
    }
}