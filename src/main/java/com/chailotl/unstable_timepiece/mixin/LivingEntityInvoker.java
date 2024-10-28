package com.chailotl.unstable_timepiece.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker
{
	@Accessor
	int getJumpingCooldown();

	@Accessor
	void setJumpingCooldown(int jumpingCooldown);
}