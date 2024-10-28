package com.chailotl.unstable_timepiece;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
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
		if (MinecraftClient.getInstance().player instanceof PlayerEntity player)
		{
			switch (Main.getTimeFlow(player))
			{
				case SLOW -> {
					tooltip.add(Text.literal("Slow").formatted(Formatting.GRAY));
					tooltip.add(Text.literal("-50% Damage Taken").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("+100% Jump Height").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("-50% Exhaustion").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("-50% Move Speed").formatted(Formatting.RED));
					tooltip.add(Text.literal("-50% Attack Speed").formatted(Formatting.RED));
					//tooltip.add(Text.literal("-50% Fall Distance").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("+100% Effect Duration").formatted(Formatting.GRAY));
				}
				case NORMAL -> {
					tooltip.add(Text.literal("Normal").formatted(Formatting.GRAY));
				}
				case FAST -> {
					tooltip.add(Text.literal("Fast").formatted(Formatting.GRAY));
					tooltip.add(Text.literal("+100% Move Speed").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("+50% Attack Speed").formatted(Formatting.BLUE));
					tooltip.add(Text.literal("+50% Damage Taken").formatted(Formatting.RED));
					tooltip.add(Text.literal("+50% Exhaustion").formatted(Formatting.RED));
					//tooltip.add(Text.literal("+50% Fall Distance").formatted(Formatting.RED));
					tooltip.add(Text.literal("-50% Effect Duration").formatted(Formatting.GRAY));
				}
			}
		}
	}
}