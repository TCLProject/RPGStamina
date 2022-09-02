package net.tclproject.rpgstamina.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import net.tclproject.rpgstamina.api.ExtendedPlayer;


public class StaminaClientRefreshMessage implements IMessage {
	private NBTTagCompound data;

	// The basic, no-argument constructor MUST be included to use the new automated handling.
	public StaminaClientRefreshMessage() {}

	// We need to initialize our data, so we provide a suitable constructor.
	public StaminaClientRefreshMessage(EntityPlayer player) {
		data = new NBTTagCompound();
		ExtendedPlayer.get(player).saveNBTData(data);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {  // Previously called decodeInto, no longer requires the context.
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {  // Previously called encodeInto, no longer requires the context.
		ByteBufUtils.writeTag(buffer, data);
	}


	public static class MessageHandler implements IMessageHandler<StaminaClientRefreshMessage, IMessage> {
	// The message handler used to define that gets called whenever the message is sent.

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(StaminaClientRefreshMessage message, MessageContext ctx) {
		// What do the when the message is received.
		// Return an IMessage object to define a response.

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			ExtendedPlayer.get(player).loadNBTData(message.data);
			return null;
		}
	}

}
