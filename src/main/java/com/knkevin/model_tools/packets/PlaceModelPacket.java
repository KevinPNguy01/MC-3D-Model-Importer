package com.knkevin.model_tools.packets;

import com.knkevin.model_tools.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A packet sent from the client to server in order to place the loaded Model.
 */
public class PlaceModelPacket {
	public PlaceModelPacket() {}

	public void encode(FriendlyByteBuf buffer) {}

	public static PlaceModelPacket decode(FriendlyByteBuf buffer) {
		return new PlaceModelPacket();
	}

	/**
	 * If the player is not null and there is a loaded Model, then place the Model.
	 */
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		if (player == null) return;
		ctx.get().enqueueWork(() -> {
			if (Main.model != null) {
				Main.model.placeBlocks(player.getLevel());
				player.sendSystemMessage(Component.literal("Successfully placed model."));
			} else player.sendSystemMessage(Component.literal("Error: No model loaded."));
		});
		ctx.get().setPacketHandled(true);
	}
}
