package com.chailotl.unstable_timepiece.mixin;

import com.chailotl.unstable_timepiece.Main;
import com.chailotl.unstable_timepiece.TimeFlow;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
	@Shadow public abstract boolean damage(DamageSource source, float amount);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Unique
	private float getScale(float slow, float fast)
	{
		return switch (Main.getTimeFlow((PlayerEntity)(Object) this))
		{
			case TimeFlow.SLOW -> slow;
			case TimeFlow.NORMAL -> 1f;
			case TimeFlow.FAST -> fast;
		};
	}

	@Override
	public void move(MovementType movementType, Vec3d movement)
	{
		super.move(movementType, getWorld().isClient
			? movement.multiply(getScale(0.5f, 2f))
			: movement);
	}

	@Override
	protected float getJumpVelocity(float strength)
	{
		return super.getJumpVelocity(strength) * getScale(2.25f, 0.675f);
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier)
	{
		return super.computeFallDamage(fallDistance * getScale(0.5f, 1.5f), damageMultiplier);
	}

	@Inject(method = "getAttackCooldownProgressPerTick", at = @At("RETURN"), cancellable = true)
	private void modifyAttackSpeed(CallbackInfoReturnable<Float> cir)
	{
		cir.setReturnValue((float) (1.0 / getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * (20.0 * getScale(1.5f, 0.75f))));
	}

	@ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyDamage(float amount, @Local(ordinal = 0) DamageSource source)
	{
		if (source.isIn(Main.BYPASSES_TIME_FLOW))
		{
			return amount;
		}
		else
		{
			return amount * getScale(0.5f, 1.5f);
		}
	}

	@ModifyVariable(method = "addExhaustion", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyExhaustion(float exhaustion)
	{
		return exhaustion * getScale(0.5f, 1.5f);
	}

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void modifyJumpingCooldown(CallbackInfo ci)
	{
		if (Main.getTimeFlow((PlayerEntity)(Object) this) == TimeFlow.FAST)
		{
			var entity = (LivingEntityInvoker) this;
			int cooldown = entity.getJumpingCooldown();

			if (cooldown > 0)
			{
				entity.setJumpingCooldown(--cooldown);
			}
		}
	}

	@Override
	public int getItemUseTimeLeft()
	{
		if (activeItemStack.isIn(Main.AFFECTED_BY_TIME_FLOW))
		{
			int maxUseTime = activeItemStack.getMaxUseTime(this);

			return (int) (maxUseTime - (maxUseTime - super.getItemUseTimeLeft()) * getScale(0.75f, 1.25f));
		}
		else
		{
			return super.getItemUseTimeLeft();
		}
	}
}