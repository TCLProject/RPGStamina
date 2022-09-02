package net.tclproject.rpgstamina.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.tclproject.rpgstamina.config.Config;


public class ConfigSyncMessage implements IMessage {
	private String data;

	// The basic, no-argument constructor MUST be included to use the new automated handling.
	public ConfigSyncMessage() {}

	// We need to initialize our data, so we provide a suitable constructor.
	public ConfigSyncMessage(String data) {
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {  // Previously called decodeInto, no longer requires the context.
		data = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {  // Previously called encodeInto, no longer requires the context.
		ByteBufUtils.writeUTF8String(buffer, data);
	}

	public static class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage> {
	// The message handler used to define that gets called whenever the message is sent.

		@Override
		public IMessage onMessage(ConfigSyncMessage message, MessageContext ctx) {
		// What do the when the message is received.
		// Return an IMessage object to define a response.

			Config.enableReplaceFood = (message.data.charAt(0) == '1');
			Config.enableReplaceShieldBar = (message.data.charAt(1) == '1');
			return null;
		}
	}

}
