package com.chailotl.unstable_timepiece;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record TimeFlowPayload(TimeFlow timeFlow) implements CustomPayload
{
	public static final CustomPayload.Id<TimeFlowPayload> ID = new CustomPayload.Id<>(Main.id("change_time_flow"));
	public static final PacketCodec<RegistryByteBuf, TimeFlowPayload> CODEC = PacketCodec.tuple(
		TimeFlow.PACKET_CODEC, TimeFlowPayload::timeFlow,
		TimeFlowPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
