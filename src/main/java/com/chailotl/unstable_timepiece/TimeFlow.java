package com.chailotl.unstable_timepiece;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum TimeFlow
{
	SLOW,
	NORMAL,
	FAST;

	public TimeFlow shift()
	{
		return switch (this)
		{
			case SLOW -> NORMAL;
			case NORMAL -> FAST;
			case FAST -> SLOW;
		};
	}

	public static final PacketCodec<ByteBuf, TimeFlow> PACKET_CODEC = PacketCodecs.INTEGER.xmap(i -> TimeFlow.values()[i], TimeFlow::ordinal);
}