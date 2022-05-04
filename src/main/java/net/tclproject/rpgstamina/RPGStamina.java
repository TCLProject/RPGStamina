package net.tclproject.rpgstamina;

import java.io.File;
import java.util.Random;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.tclproject.rpgstamina.command.Command;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.tclproject.mysteriumlib.network.MagicNetwork;
import net.tclproject.rpgstamina.handler.GuiHealthStamina;
import net.tclproject.rpgstamina.handler.StEventHandler;

@Mod(modid = RPGStamina.MODID, useMetadata = true, version = RPGStamina.VERSION, name = "RPG Stamina")
public class RPGStamina {
    public static final String MODID = "rpgstamina";
    public static final String VERSION = "2.0a";
    public static Random rand = new Random();
    public static StEventHandler EVENT_HANDLER;
    public Logger logger;
    public File configFolder;
    boolean obfuscated;
    int counter = 0;
	public File modDir;

    @Instance("rpgstamina")
    public static RPGStamina instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    	
        EVENT_HANDLER = new StEventHandler();
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        FMLCommonHandler.instance().bus().register(EVENT_HANDLER);

        this.logger = event.getModLog();

    	Config.init(event.getModConfigurationDirectory().toString(), event);

    	instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	MagicNetwork.registerPackets();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    	this.obfuscated = !(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");

        // Config changes loading
        Config.postInit();
        if (Config.enableBlazeRodRender) Items.blaze_rod.setFull3D();
    	
    	if (FMLCommonHandler.instance().getEffectiveSide().isClient()) MinecraftForge.EVENT_BUS.register(new GuiHealthStamina(Minecraft.getMinecraft()));
    	
    	// All current and future thaum support
    	// if (Loader.isModLoaded("Thaumcraft")) {
    	// 	FMLLog.log("Mysterium Patches", Level.ALL, "Thaumcraft support enabled for RPGStamina.");
    	// }
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        if (Config.commandEnabled) event.registerServerCommand(new Command());
    }
}
