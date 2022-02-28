package net.tclproject.mysteriumlib.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ServerMagicPacket<T extends IMessage> extends MagicPacket<T> {
// implementing a final version of the server message handler both prevents it from
// appearing automatically and prevents us from ever accidentally overriding it
	public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
		System.out.println("Client side handling occured for a server side packet. THIS SHOULD NOT HAPPEN! REPORT THIS TO THE MOD AUTHOR!");
		return null;
	}
}
