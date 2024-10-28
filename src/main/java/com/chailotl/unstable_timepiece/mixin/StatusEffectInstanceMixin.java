package com.chailotl.unstable_timepiece.mixin;

import com.chailotl.unstable_timepiece.Main;
import com.chailotl.unstable_timepiece.TimeFlow;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin
{
	@Unique private boolean halfTick = false;

	@Shadow private int duration;

	@Shadow protected abstract boolean isActive();

	@Shadow protected abstract int updateDuration();

	@Shadow public abstract int mapDuration(Int2IntFunction mapper);

	@Inject(method = "update", at = @At("HEAD"))
	private void modifyDuration(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> cir)
	{
		if (isActive() && entity instanceof PlayerEntity player)
		{
			switch (Main.getTimeFlow(player))
			{
				case TimeFlow.SLOW -> {
					if (halfTick)
					{
						duration = mapDuration(duration -> duration + 1);
					}
					halfTick = !halfTick;
				}
				case TimeFlow.FAST -> {
					if (duration % 2 == 1)
					{
						updateDuration();
					}
				}
			}
		}
	}

	@WrapOperation(
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"
		)
	)
	private boolean modifyApplication(StatusEffect instance, int duration, int amplifier, Operation<Boolean> original, @Local(ordinal = 0, argsOnly = true) LivingEntity entity)
	{
		if (entity instanceof PlayerEntity player)
		{
			switch (Main.getTimeFlow(player))
			{
				case SLOW -> duration = duration * 2 - (halfTick ? 0 : 1);
				case FAST -> {
					duration *= 2;
					 return original.call(instance, duration, amplifier)
						 || original.call(instance, duration - 1, amplifier);
				}
			}
		}

		return original.call(instance, duration, amplifier);
	}
}