package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BehelitItem extends Item {
    public BehelitItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof ServerPlayer player)) return false;
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        ServerLevel level = player.server.getLevel(ModDimensions.THE_HAND_KEY);
        if (level == null) return false;

        player.teleportTo(level, 0.5, 70, 0.5, player.getYRot(), player.getXRot());
        targetPlayer.teleportTo(level, 0.5, 70, 0.5, targetPlayer.getYRot(), targetPlayer.getXRot());

player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));


        return super.hurtEnemy(stack, target, attacker);
    }
}
