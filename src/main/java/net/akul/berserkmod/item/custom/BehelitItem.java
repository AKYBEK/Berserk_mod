package net.akul.berserkmod.item.custom;

import net.akul.berserkmod.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BehelitItem extends Item {
    public BehelitItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return false;
        if (player.level().isClientSide) return false;

        // Добавляем эффект тьмы атакующему
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        
        // Если цель - игрок, добавляем эффект и ему
        if (target instanceof Player targetPlayer) {
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, false));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Попытка телепортации в измерение "Рука Бога"
            ServerLevel targetLevel = serverPlayer.server.getLevel(ModDimensions.THE_HAND_KEY);
            if (targetLevel != null) {
                serverPlayer.teleportTo(targetLevel, 0.5, 70, 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.sendSystemMessage(Component.literal("Вы попали в длань Господа").withStyle(net.minecraft.ChatFormatting.DARK_RED));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("Измерение недоступно.").withStyle(net.minecraft.ChatFormatting.DARK_RED));
            }
        }


        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
