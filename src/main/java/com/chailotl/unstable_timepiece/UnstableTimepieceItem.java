package com.chailotl.unstable_timepiece;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class UnstableTimepieceItem extends Item
{
	public UnstableTimepieceItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		TimeFlow timeFlow = Main.getTimeFlow(user).shift();
		if (!world.isClient)
		{
			user.setAttached(Main.TIME_FLOW, timeFlow);
			ServerPlayNetworking.send((ServerPlayerEntity) user, new TimeFlowPayload(timeFlow));
			user.sendMessage(Text.translatable(switch (timeFlow) {
				case SLOW -> "gui.unstable_timepiece.slow";
				case NORMAL -> "gui.unstable_timepiece.normal";
				case FAST -> "gui.unstable_timepiece.fast";
			}), true);
		}

		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.5f, switch (timeFlow) {
			case SLOW -> 0.5f;
			case NORMAL -> 0.707107f;
			case FAST -> 1f;
		});

		user.getItemCooldownManager().set(this, 10);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
		{
			UnstableTimepieceTooltip.appendTooltip(tooltip);
		}
	}
}