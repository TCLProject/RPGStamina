package net.tclproject.mysteriumlib.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ClientMagicPacket<T extends IMessage> extends MagicPacket<T> {

	public final IMessage handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {
		System.out.println("Server side handling occured for a client side packet. THIS SHOULD NOT HAPPEN! REPORT THIS TO THE MOD AUTHOR!");
		return null;
	}
}