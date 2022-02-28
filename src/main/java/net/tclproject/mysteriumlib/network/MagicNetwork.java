package net.tclproject.mysteriumlib.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;

public final class MagicNetwork {
	
	private static int id =  0;
	
	public static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("rpgstamina");
	
	public static final void registerPackets() {
       	// Registration
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			// Client Packets
			MagicNetwork.registerPacket(net.tclproject.rpgstamina.packets.StaminaRefreshClient.Handler.class, net.tclproject.rpgstamina.packets.StaminaRefreshClient.class);
		} else {
			// Server Packets
		}
    }
	
	/**
     * Sending a packet to all in a radius.
     * @param world - world
     * @param distance - radius in which the packet will be sent
     */
    public static void sendToAllAround(IMessage packet, World world, double x, double y, double z, double distance) {
    	dispatcher.sendToAllAround(packet, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, distance));
    }
    
	private static final <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ>  messageClass) {
    	Side side = ClientMagicPacket.class.isAssignableFrom(handlerClass) ? Side.CLIENT : Side.SERVER;
        dispatcher.registerMessage(handlerClass, messageClass, id++, side);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private static final void registerParallelPacket(Class handlerClass, Class messageClass) {
		 if (ParallelMagicPacket.class.isAssignableFrom(handlerClass)) {
			 dispatcher.registerMessage(handlerClass, messageClass, id++, Side.CLIENT);
			 dispatcher.registerMessage(handlerClass, messageClass, id++, Side.SERVER);
		 } else {
		 throw new IllegalArgumentException("Cannot register " + handlerClass.getName() + " on both sides - must extend ParallelMagicPacket!");
		 }
		}

}
