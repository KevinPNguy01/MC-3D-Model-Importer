package com.knkevin.model_tools.packets;

import com.knkevin.model_tools.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A packet sent from the client to server in order to undo the last placement of the loaded Model.
 */
public class UndoModelPacket {
	public UndoModelPacket() {}

	public void encode(FriendlyByteBuf buffer) {}

	public static UndoModelPacket decode(FriendlyByteBuf buffer) {
		return new UndoModelPacket();
	}

	/**
	 * If the player is not null and there is a loaded Model, then undo the last placement of the Model.
	 */
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		if (player == null) return;
		ctx.get().enqueueWork(() -> {
			if (Main.model != null) {
				Main.model.undo(player.getLevel());
				player.sendSystemMessage(Component.literal("Undo successful."));
			} else player.sendSystemMessage(Component.literal("Error: No model loaded."));
		});
		ctx.get().setPacketHandled(true);
	}
}
