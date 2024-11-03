package com.chailotl.unstable_timepiece;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class UnstableTimepieceTooltip
{
	public static void appendTooltip(List<Text> tooltip)
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