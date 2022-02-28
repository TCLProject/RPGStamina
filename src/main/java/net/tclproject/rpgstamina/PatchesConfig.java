package net.tclproject.rpgstamina;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class PatchesConfig {
	
	public static Configuration config;
	public static boolean enableBlazeRodRender = true;
	public static boolean enableReplaceFood = false;
	public static boolean preventJump = false;
	
	public static boolean staminaDrainJump = true;
	public static boolean staminaDrainSprint = true;
	public static boolean staminaDrainUse = true;
	public static boolean staminaDrainGun = true;
	public static boolean staminaDrainHit = true;

	public static boolean enchantmentEnabled = true;
	public static boolean potionEnabled = true;
	public static boolean baubleEnabled = true;
	
	public static float foodMultiplier = 1;
	public static int defaultMaxStamina = 200;
	public static int defaultStaminaWithLevel = 25;
	public static int defaultStaminaForJump = 3;
	public static int defaultStaminaForGun = 5;
	public static int defaultStaminaForHit = 5;
	public static int defaultStaminaForSprint = 10;
	public static int defaultStaminaRegen = 3;
	public static int defaultEnchantmentID = 125;
	public static int defaultPotionID = 30;
	
	public static int staminaXOffset = 0;
	public static int staminaYOffset = 0;
	
	public static String CATEGORY_VALUES = "Values";
	public static String CATEGORY_MECHANICS = "Mechanics";
	public static String CATEGORY_RENDER = "Visuals";
	public static boolean staminaEmptyBar;

	public static void init(String configDir, FMLPreInitializationEvent event) {
		
		FMLCommonHandler.instance().bus().register(new PatchesConfig());
		
		if (config == null) {
			File path = new File(configDir + "/" + "rpgstamina.cfg");
			config = new Configuration(path);
			loadConfiguration();
		}
	}
	
	private static void loadConfiguration() {
		staminaXOffset = config.getInt("Position X Offset", CATEGORY_RENDER, 0, -10000, 1000, "By how much the render of the stamina bar is offset left/right. For times it conflicts with other mods' bars positions.");
		staminaYOffset = config.getInt("Position Y Offset", CATEGORY_RENDER, 0, -10000, 1000, "By how much the render of the stamina bar is offset up/down. For times it conflicts with other mods' bars positions.");
		staminaDrainJump = config.getBoolean("Apply Penalty for Jumping", CATEGORY_MECHANICS, true, "If jumping drains stamina.");
		staminaDrainGun = config.getBoolean("Apply Penalty for Shooting a Gun", CATEGORY_MECHANICS, true, "If shooting drains stamina. Part of Modern Warfare 2 integration.");
		preventJump = config.getBoolean("Prevent Jump", CATEGORY_MECHANICS, false, "If the mod prevents you from jumping when out of stamina.");
		staminaDrainSprint = config.getBoolean("Apply Penalty for Sprinting", CATEGORY_MECHANICS, true, "If sprinting drains stamina.");
		staminaDrainUse = config.getBoolean("Apply Penalty for Using", CATEGORY_MECHANICS, true, "If using items drains stamina.");
		staminaDrainHit = config.getBoolean("Apply Penalty for Hitting", CATEGORY_MECHANICS, true, "If hitting mobs drains stamina.");
		enchantmentEnabled = config.getBoolean("Enchantment Enabled", CATEGORY_MECHANICS, true, "Option to disable the light feet enchantment.");
		potionEnabled = config.getBoolean("Potion Enabled", CATEGORY_MECHANICS, true, "Option to disable the endurance potion.");
		baubleEnabled = config.getBoolean("Bauble Enabled", CATEGORY_MECHANICS, true, "Option to disable the endurance ring. Ring only present with potion enabled and baubles installed.");
		staminaEmptyBar = config.getBoolean("Empty Stamina Bar Background", CATEGORY_RENDER, true, "Option to disable the background of the stamina bar.");

		foodMultiplier = config.getFloat("Food Multiplier", CATEGORY_VALUES, 1.0F, 0.01F, 100F, "Multiplier for how much stamina gets regenerated with eating food.");
		enableBlazeRodRender = config.getBoolean("Enable Blaze Rod Render", CATEGORY_RENDER, true, "Makes the blaze rod render like a sword instead of like a normal item. This is the case in 1.8+.");
		enableReplaceFood = config.getBoolean("Replace Food Completely", CATEGORY_MECHANICS, false, "Replaces the current food mechanics in minecraft completely instead of adding to them. Option for those who think they're boring.");
		defaultMaxStamina = config.getInt("Default Max Stamina", CATEGORY_VALUES, 200, 0, Integer.MAX_VALUE, "The amount of stamina each player starts with.");
		defaultStaminaWithLevel = config.getInt("Gain With Each Level", CATEGORY_VALUES, 25, 0, Integer.MAX_VALUE, "The amount of max stamina a player gains for each level of xp.");
		defaultStaminaForJump = config.getInt("Jump Penalty", CATEGORY_VALUES, 3, 0, Integer.MAX_VALUE, "The amount of stamina a player loses when jumping.");
		defaultStaminaForGun = config.getInt("Shoot Gun Penalty", CATEGORY_VALUES, 5, 0, Integer.MAX_VALUE, "The amount of stamina a player loses when shooting a gun. Part of Modern Warfare 2 integration.");
		defaultStaminaForHit = config.getInt("Hit Penalty", CATEGORY_VALUES, 5, 0, Integer.MAX_VALUE, "The amount of stamina a player loses when hitting and entity.");
		defaultStaminaForSprint = config.getInt("Sprint or Use Penalty", CATEGORY_VALUES, 10, 0, Integer.MAX_VALUE, "The amount of stamina a player loses for each second of sprinting or using an item.");
		defaultStaminaRegen = config.getInt("Regeneration Amount", CATEGORY_VALUES, 3, 0, Integer.MAX_VALUE, "The amount of stamina a player regenerates for every second of not having done intensive activities.");
		defaultEnchantmentID = config.getInt("Light Feet Enchantment ID", CATEGORY_VALUES, 125, 0, Integer.MAX_VALUE, "ID of the enchantment - change if conflicts. Unless using Enchantment ID Extender, maximum is 255.");
		defaultPotionID = config.getInt("Endurance Potion ID", CATEGORY_VALUES, 30, 0, Integer.MAX_VALUE, "ID of the potion - change if conflicts. Unless using Extended Potions, DragonAPI, Enchantment ID Extender or other extender mod, maximum is 31.");
		if (config.hasChanged()) {
			config.save();
		}
	
	}
	
	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent event) {
		
		if (event.modID.equalsIgnoreCase("mysteriumpatches")) {
			loadConfiguration();
		}
		
	}
	
	public static Configuration getConfiguration() {
		return config;
	}
	

}
