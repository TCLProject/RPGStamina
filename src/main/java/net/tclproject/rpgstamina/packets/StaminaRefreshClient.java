package net.tclproject.rpgstamina.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.tclproject.rpgstamina.handler.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.tclproject.mysteriumlib.network.ClientMagicPacket;

public class StaminaRefreshClient implements IMessage {
	
	private NBTTagCompound data;

 // The basic, no-argument constructor MUST be included to use the new automated handling
	public StaminaRefreshClient() {}

 // We need to initialize our data, so provide a suitable constructor:
	public StaminaRefreshClient(EntityPlayer player) {
		data = new NBTTagCompound();
		ExtendedPlayer.get(player).saveNBTData(data);
 }

	@Override
 	public void fromBytes(ByteBuf buffer) {
	 	data = ByteBufUtils.readTag(buffer);
 	}

	 @Override
	 public void toBytes(ByteBuf buffer) {
		 ByteBufUtils.writeTag(buffer, data);
	 }
	 
	 public static class Handler extends ClientMagicPacket<StaminaRefreshClient> {
		 
			@Override
			public IMessage handleClientMessage(EntityPlayer player, StaminaRefreshClient message, MessageContext ctx) {
				ExtendedPlayer.get(player).loadNBTData(message.data);
				return null;
			}

		}

}


