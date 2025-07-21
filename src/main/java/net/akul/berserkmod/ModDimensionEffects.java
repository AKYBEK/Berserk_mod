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
        // Темно-красный туман цвета #8B0000
        return new Vec3(0.545, 0.0, 0.0);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return true;
    }

    @Override
    public float getCloudHeight() {
        return Float.NaN;
    }

    @Override
    public Vec3 getSkyColor(float celestialAngle, float partialTicks) {
        // Темно-красное небо #8B0000
        return new Vec3(0.545, 0.0, 0.0);
    }

    @Override
    public float[] getSunriseColor(float celestialAngle, float partialTicks) {
        // Убираем эффект восхода/заката
        return null;
    }

    @Override
    public boolean forceBrightLightmap() {
        return true;
    }

    @Override
    public boolean constantAmbientLight() {
        return true;
    }
}