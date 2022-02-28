package net.tclproject.mysteriumlib.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public abstract class MagicPacket<T extends IMessage> implements IMessageHandler <T, IMessage> {
	
	@SideOnly(Side.CLIENT)
	public abstract IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx);

	/**
	* Handle a message received on the server side
	* @return a message to send back to the Client, or null if no reply is necessary
	*/
	public abstract IMessage handleServerMessage(EntityPlayer player, T message, MessageContext ctx);

	@Override
	public IMessage onMessage(T message, MessageContext ctx) {
		if (ctx.side.isClient()) {
			return handleClientMessage(getPlayerEntity(ctx), message, ctx);
		} else {
			return handleServerMessage(getPlayerEntity(ctx), message, ctx);
		}
	}
	
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity);
	}
}
