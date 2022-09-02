package net.tclproject.rpgstamina.network;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.shield.IShield;
import net.tclproject.rpgstamina.config.Config;
import net.tclproject.rpgstamina.EventHandler;
import net.tclproject.rpgstamina.api.ExtendedPlayer;


public class SpecialNeedsActionMessage implements IMessage {
	private String data;

	// The basic, no-argument constructor MUST be included to use the new automated handling.
	public SpecialNeedsActionMessage() {}

	// We need to initialize our data, so we provide a suitable constructor.
	public SpecialNeedsActionMessage(String data) {
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

	public static class MessageHandler implements IMessageHandler<SpecialNeedsActionMessage, IMessage> {
	// The message handler used to define that gets called whenever the message is sent.

		@Override
		public IMessage onMessage(SpecialNeedsActionMessage message, MessageContext ctx) {
		// What do the when the message is received.
		// Return an IMessage object to define a response.

			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ExtendedPlayer extended = ExtendedPlayer.get(player);

			if (message.data.startsWith("battlegearShieldBashed") && Loader.isModLoaded("battlegear2")) {
				ItemStack offhandItem = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

				if (extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE) && offhandItem != null && offhandItem.getItem() instanceof IShield) {
					Object internalAttackValue = Config.internalItemAttackDict.get(offhandItem.getItem());

					int value = Config.defaultStaminaGainForBattlegearShieldBash;
					if (internalAttackValue != null) value = (int) internalAttackValue;

					extended.gainStamina(value);
					if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
				}
			}

			return null;
		}
	}

}
