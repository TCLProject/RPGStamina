package net.tclproject.rpgstamina.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.tclproject.rpgstamina.ModProperties;


public class Network {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModProperties.MODID);

	public static void init() {

		// Registers a packet, using the message handler class and the message class.
		// The discriminator is the unique packet ID (range: 0-255). The side is the side that will receive the packet.
		INSTANCE.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(StaminaClientRefreshMessage.MessageHandler.class, StaminaClientRefreshMessage.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(SpecialNeedsActionMessage.MessageHandler.class, SpecialNeedsActionMessage.class, 2, Side.SERVER);
	}
}
