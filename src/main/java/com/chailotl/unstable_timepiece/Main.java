package com.chailotl.unstable_timepiece;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer
{
	public static final String MOD_ID = "unstable_timepiece";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final UnstableTimepieceItem UNSTABLE_TIMEPIECE = Registry.register(
		Registries.ITEM,
		id("unstable_timepiece"),
		new UnstableTimepieceItem(new Item.Settings().maxCount(1))
	);

	public static final AttachmentType<TimeFlow> TIME_FLOW = AttachmentRegistry.<TimeFlow>builder()
		.initializer(() -> TimeFlow.NORMAL)
		.buildAndRegister(id("time_flow"));

	public static final TagKey<DamageType> BYPASSES_TIME_FLOW = TagKey.of(RegistryKeys.DAMAGE_TYPE, id("bypasses_time_flow"));
	public static final TagKey<Item> AFFECTED_BY_TIME_FLOW = TagKey.of(RegistryKeys.ITEM, id("affected_by_time_flow"));

	@Override
	public void onInitialize()
	{
		//LOGGER.info("Hello Fabric world!");

		PayloadTypeRegistry.playS2C().register(TimeFlowPayload.ID, TimeFlowPayload.CODEC);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(UNSTABLE_TIMEPIECE);
		});
	}

	public static Identifier id(String path)
	{
		return Identifier.of(MOD_ID, path);
	}

	public static TimeFlow getTimeFlow(PlayerEntity player)
	{
		TimeFlow timeFlow = player.getAttached(TIME_FLOW);
		return timeFlow != null ? timeFlow : TimeFlow.NORMAL;
	}
}