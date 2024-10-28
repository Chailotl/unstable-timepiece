package com.chailotl.unstable_timepiece;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientMain implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(TimeFlowPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				context.player().setAttached(Main.TIME_FLOW, payload.timeFlow());
			});
		});
	}
}
