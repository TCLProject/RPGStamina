package net.tclproject.rpgstamina.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.item.Item;
import net.tclproject.rpgstamina.ModProperties;
import net.tclproject.rpgstamina.config.gui.GuiConfig;


public class Config {
	public static Configuration config;
	public static String configFolder;

	// Food category
	public static boolean enableReplaceFood = true;
	public static int defaultFoodStaminaGain = 2;
	public static int defaultFoodHealthValue = 0;
	public static String[] customFoodValues = new String[]{"minecraft:golden_carrot:50:5"};
	public static HashMap<Item, Integer> internalFoodStaminaDict = new HashMap<>();
	public static HashMap<Item, Integer> internalFoodHealthDict = new HashMap<>();

	// Mechanics category
	public static boolean enchantmentEnabled = true;
	public static boolean potionEnabled = true;
	public static boolean baubleEnabled = true;
	public static boolean commandEnabled = true;
	public static int enchantmentID = 125;
	public static int potionID = 30;

	public static int additionalStagesAmount = 2;
	public static PlayerStage[] stageList = new PlayerStage[0];
	public static PlayerStage[] defaultStageValues = new PlayerStage[]{
			new PlayerStage("disabled", false, false, false, false, false, new String[0], new String[0]), // STAGE 0 (No stage)
			new PlayerStage("0", false, false, true, true, true, new String[0], new String[]{"2:1:false", "4:1:false", "18:1:false"}), // STAGE 1
			new PlayerStage("25", false, false, false, false, false, new String[0], new String[]{"2", "4:1", "18"}), // STAGE 2
	};

	// Natural regeneration category
	public static int naturalRegenDelay = 7;
	public static int naturalRegenStaminaGain = 5;
	public static int naturalRegenRequiredFoodAmount = 1;
	public static int naturalRegenRequiredSaturationAmount = 0;

	// Values category
	public static int defaultMaxStamina = 200;
	public static int defaultStamina = 200;
	public static int staminaCapacityLevelUPPerEXPLevel = 25;
	public static int staminaAmountAfterDeath = -1;

	// Vanilla gains
	public static int staminaGainForJump = -3;
	public static int staminaGainForSprintJump = -7;
	public static int staminaGainForSprint = -5;
	public static int defaultStaminaGainForItemUse = -5;
	public static int defaultStaminaGainForAttack = -5;
	public static String[] customItemValues = new String[0];
	public static HashMap<Item, Integer> internalItemAttackDict = new HashMap<>();
	public static HashMap<Item, Integer> internalItemUseDict = new HashMap<>();

	// Modern Warfare 2 integration
	public static int staminaGainForMWGun = -5;

	// Battlegear integration
	public static boolean enableReplaceShieldBar = false;
	public static int defaultStaminaGainForBattlegearShieldBash = -15;
	public static boolean enableDualBowSupport = true;

	// Visuals category
	public static int staminaHUDXOffset = 0;
	public static int staminaHUDYOffset = 0;
	public static int staminaHUDHookElement = 0;
	public static boolean allowOxygenBarOverlap = false;
	public static boolean staminaEmptyBar = true;
	public static boolean enableBlazeRodRender = true;
	public static boolean mirrorBar = false;


	// Category names
	public static String CATEGORY_MECHANICS = "mechanics";
	public static String CATEGORY_VISUALS = "visuals";
	public static String CATEGORY_FOOD = "food";
	public static String CATEGORY_NOSTAGE = "stages: no stage";
	public static String CATEGORY_STAGEAMOUNT = "stages: amount of stages";
	public static String CATEGORY_STAGE = "stages: stage ";
	public static String CATEGORY_REGENERATION = "values: natural regeneration";
	public static String CATEGORY_VALUES = "values: global values";
	public static String CATEGORY_VANILLA = "values: vanilla actions";
	public static String CATEGORY_MODERNWARFARE = "values (mod integration): modern warfare 2";
	public static String CATEGORY_BATTLEGEAR = "values (mod integration): battlegear 2 / theoffhandmod";


	public static void init() {
		if (config == null) config = new Configuration(new File(configFolder + "/" + "rpgstamina.cfg"));

		config.load();
		sync();

		FMLCommonHandler.instance().bus().register(new Config());
	}


	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(ModProperties.MODID)) sync();
	}

	private static void mapItem(String item, HashMap<Item, Integer> map) {
	// This function will correctly map an item from the ModID:Item:Value format to a given HashMap.

		if (item.contains(":")) {
			String[] splittedItem = item.split(":");

			try {
				if (splittedItem.length >= 3) map.put(GameRegistry.findItem(splittedItem[0], splittedItem[1]), Integer.parseInt(splittedItem[2]));
			}
			catch (NumberFormatException ignore) {}
		}
	}


	public static void sync() {
	// This function is responsible for updating the internal variables based on what is set in the config file.

		// Visuals category
		config.getCategory(CATEGORY_VISUALS).setComment("Settings related to how the stamina bar gets rendered and other visual tweaks.");
		staminaHUDXOffset = config.getInt("HUD Position X Offset", CATEGORY_VISUALS, staminaHUDXOffset, -10000, 1000, "By how much the render of the stamina bar is offset left/right. For times it conflicts with other mods' bars positions.");
		staminaHUDYOffset = config.getInt("HUD Position Y Offset", CATEGORY_VISUALS, staminaHUDYOffset, -10000, 1000, "By how much the render of the stamina bar is offset up/down. For times it conflicts with other mods' bars positions.");
		staminaHUDHookElement = config.getInt("HUD Hook element", CATEGORY_VISUALS, staminaHUDHookElement, 0, 9, "The HUD element where the stamina bar is attached. Only change this if you have compatibility issues.  0 = Auto, any other number is a specific hud element.");
		staminaEmptyBar = config.getBoolean("Empty stamina bar background", CATEGORY_VISUALS, staminaEmptyBar, "Option to disable the background of the stamina bar.");
		enableBlazeRodRender = config.getBoolean("Enable blaze rod render", CATEGORY_VISUALS, enableBlazeRodRender, "Makes the blaze rod render like a sword instead of like a normal item. This is the case in 1.8+.");
		mirrorBar = config.getBoolean("Mirror the bar direction", CATEGORY_VISUALS, mirrorBar, "Mirrors the stamina bar to go from left to right instead of right to left.");
		allowOxygenBarOverlap = config.getBoolean("Allow overlap with the oxygen bar", CATEGORY_VISUALS, allowOxygenBarOverlap, "Disables the patches made to the oxygen bar, allows both bars to overlap in case one mod changes it.");

		// Mechanics category
		config.getCategory(CATEGORY_MECHANICS).setComment("Settings related to additional features like potion effects, enchants, commands etc.");
		commandEnabled = config.getBoolean("Command enabled", CATEGORY_MECHANICS, commandEnabled, "Whether to enable the /stamina command.");
		enchantmentEnabled = config.getBoolean("Enchantment enabled", CATEGORY_MECHANICS, enchantmentEnabled, "Whether to enable the light feet enchantment.");
		potionEnabled = config.getBoolean("Potion enabled", CATEGORY_MECHANICS, potionEnabled, "Whether to enable the endurance potion.");
		baubleEnabled = config.getBoolean("Bauble enabled", CATEGORY_MECHANICS, baubleEnabled, "Whether to enable the endurance ring. (Requires the endurance potion to be enabled and the baubles mod installed).");
		enchantmentID = config.getInt("Light Feet Enchantment ID", CATEGORY_MECHANICS, enchantmentID, 0, Integer.MAX_VALUE, "ID of the Light Feet enchantment - change if conflicts. Unless using Enchantment ID Extender, maximum is 255.");
		potionID = config.getInt("Endurance Potion ID", CATEGORY_MECHANICS, potionID, 0, Integer.MAX_VALUE, "ID of the endurance potion - change if conflicts. Unless using Extended Potions, DragonAPI, Enchantment ID Extender or other extender mod, maximum is 31.");


		// Food category
		config.getCategory(CATEGORY_FOOD).setComment("Settings related to the food specific settings.");
		enableReplaceFood = config.getBoolean("Replace food completely", CATEGORY_FOOD, enableReplaceFood,  "Whether the mod should completely replace the current food mechanics in minecraft instead of adding to them. Use if you're bored of the Minecraft hunger system.");
		defaultFoodStaminaGain = config.getInt("Default food stamina gain", CATEGORY_FOOD, defaultFoodStaminaGain, Integer.MIN_VALUE, Integer.MAX_VALUE, "Defines how much stamina gets regenerated by eating food if it doesn't have a specific value defined.");
		defaultFoodHealthValue = config.getInt("Default food health replenish value", CATEGORY_FOOD, defaultFoodHealthValue, Integer.MIN_VALUE, Integer.MAX_VALUE, "Defines how much health gets regenerated by eating food if it doesn't have a specific value defined.");

		customFoodValues = config.getStringList("Custom food values (ModID:Item:Stamina:(Health))", CATEGORY_FOOD, customFoodValues, "Custom item-specific values for food items. If not set, it will use the default value instead.");
		for (String s : customFoodValues) {
			String[] splitted = s.split(":");
			if (splitted.length >= 3) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[2], internalFoodStaminaDict);
			if (splitted.length >= 4) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[3], internalFoodHealthDict);
		}


		// Stage amount category
		config.getCategory(CATEGORY_STAGEAMOUNT).setComment("Set how many additional stages to have (doesn't apply to the \"no stage\" category).");
		additionalStagesAmount = config.getInt("Amount of additional stages", CATEGORY_STAGEAMOUNT, additionalStagesAmount, 0, Integer.MAX_VALUE, "The amount of additional stages to add along the \"no stage\" one (needs a restart to generate the config)");
		stageList = new PlayerStage[additionalStagesAmount + 1];

		// Stages
		for (int i = 0; i < stageList.length; i += 1) {
			PlayerStage defaultSetting = new PlayerStage();
			if (i < defaultStageValues.length) defaultSetting = defaultStageValues[i];

			stageList[i] = new PlayerStage();

			String endComment = "when in this stage.";
			String category = CATEGORY_STAGE + i;
			if (i == 0) {
				endComment = "when not in a stage.";
				category = CATEGORY_NOSTAGE;
			}

			config.getCategory(category).setComment("Settings that only get applied " + endComment);

			if (i > 0) stageList[i].stageMaximumValue = config.getString("Stage starting stamina amount", CATEGORY_STAGE + i, defaultSetting.stageMaximumValue, "Amount of stamina at or below which you enter this stage");

			stageList[i].preventJump = config.getBoolean("Prevent jump", category, defaultSetting.preventJump, "Whether the mod prevents you from jumping " + endComment);
			stageList[i].preventSprint = config.getBoolean("Prevent sprint", category, defaultSetting.preventSprint, "Whether the mod prevents you from sprinting " + endComment);
			stageList[i].preventAttack = config.getBoolean("Prevent attack", category, defaultSetting.preventAttack, "Whether the mod prevents you from attacking " + endComment);

			stageList[i].itemUseStrings = config.getStringList("Item usage list (ModID:Item)", category, defaultSetting.itemUseStrings, "List of items to block (or allow) usage of " + endComment);
			for (String s : stageList[i].itemUseStrings) {
				String[] splittedItem = s.split(":");
				if (splittedItem.length >= 2) stageList[i].internalItemUseList.add(GameRegistry.findItem(splittedItem[0], splittedItem[1]));
			}

			stageList[i].useWhitelistForItemUseList = config.getBoolean("Item usage whitelist instead of blacklist", category, defaultSetting.useWhitelistForItemUseList, "Whether the item use list should be a white or blacklist " + endComment);

			stageList[i].preventFoodEating = config.getBoolean("Prevent food eating", category, defaultSetting.preventFoodEating, "Whether the mod prevents you from eating food " + endComment + " (Requires food replacement to be enabled).");
			stageList[i].potionEffects = config.getStringList("Potion effects list (PotionID:(Amplifier):(showParticles))", category, defaultSetting.potionEffects, "List of potion effects to apply " + endComment);
		}

		// Natural regeneration category
		naturalRegenStaminaGain = config.getInt("Natural regeneration gain", CATEGORY_REGENERATION, naturalRegenStaminaGain, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for every second of not gaining (or losing) stamina through other means.");
		naturalRegenDelay = config.getInt("Natural regeneration delay", CATEGORY_REGENERATION, naturalRegenDelay, 1, Integer.MAX_VALUE, "The amount of time in half-seconds the player would have to not have gained (or lost) stamina to let the natural regen begin.");
		naturalRegenRequiredFoodAmount = config.getInt("Required food amount", CATEGORY_REGENERATION, naturalRegenRequiredFoodAmount, 0, 20, "The amount of food the player needs to have before natural regeneration can start to kick in. Only useful if \"Replace food completely\" is disabled.");
		naturalRegenRequiredSaturationAmount = config.getInt("Required saturation amount", CATEGORY_REGENERATION, naturalRegenRequiredSaturationAmount, 0, 20, "The amount of food saturation the player needs to have before natural regeneration can start to kick in. Only useful if \"Replace food completely\" is disabled.");


		// Global values category
		config.getCategory(CATEGORY_VALUES).setComment("Settings related to general values of the mod.");
		defaultMaxStamina = config.getInt("Default stamina capacity", CATEGORY_VALUES, defaultMaxStamina, 0, Integer.MAX_VALUE, "The amount of stamina capacity each player starts with.");
		defaultStamina = config.getInt("Default stamina amount", CATEGORY_VALUES, defaultStamina, 0, Integer.MAX_VALUE, "The amount of stamina each player starts with.");
		staminaAmountAfterDeath = config.getInt("Amount of stamina after death", CATEGORY_VALUES, staminaAmountAfterDeath, -2, Integer.MAX_VALUE, "The amount of stamina the player will have after dying and respawning. -2: Keep previous stamina amount, -1: Have maximum stamina, X: Have X stamina");
		staminaCapacityLevelUPPerEXPLevel = config.getInt("Stamina capacity level up with each XP level", CATEGORY_VALUES, staminaCapacityLevelUPPerEXPLevel, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina capacity a player gains (or loses) for each level of xp.");

		// Vanilla actions category
		config.getCategory(CATEGORY_VANILLA).setComment("Settings related to vanilla actions such as jumping, sprinting etc.");
		staminaGainForJump = config.getInt("Jump gain", CATEGORY_VANILLA, staminaGainForJump, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when jumping.");
		staminaGainForSprint = config.getInt("Sprint gain", CATEGORY_VANILLA, staminaGainForSprint, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for each half-second of sprinting.");
		staminaGainForSprintJump = config.getInt("Sprint jump gain", CATEGORY_VANILLA, staminaGainForSprint, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when jumping while sprinting.");
		defaultStaminaGainForItemUse = config.getInt("Default item use gain", CATEGORY_VANILLA, defaultStaminaGainForItemUse, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for each half-second of using an item.");
		defaultStaminaGainForAttack = config.getInt("Default attack gain", CATEGORY_VANILLA, defaultStaminaGainForAttack, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when attacking an entity.");

		customItemValues = config.getStringList("Custom item gains (ModID:Item:UseGain:(AttackGain))", CATEGORY_VANILLA, customItemValues, "Custom item-specific values for item usage and attack gains. If not set, it will use the default value instead.");
		for (String s : customItemValues) {
			String[] splitted = s.split(":");
			if (splitted.length >= 3) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[2], internalItemUseDict);
			if (splitted.length >= 4) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[3], internalItemAttackDict);
		}

		// Modern warfare integration category
		// This category only gets added if the ModernWarfare mod is present.
		if (Loader.isModLoaded("mw")) {
			config.getCategory(CATEGORY_MODERNWARFARE).setComment("Settings related to Modern Warfare 2 actions. Only shown with Modern Warfare 2 installed.");
			staminaGainForMWGun = config.getInt("Shoot Modern Warfare gun gain", CATEGORY_MODERNWARFARE, staminaGainForMWGun, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when shooting a gun.");
		}

		// Battlegear 2 integration category
		// This category only gets added if the Battlegear2 mod is present.
		if (Loader.isModLoaded("battlegear2")) {
			config.getCategory(CATEGORY_BATTLEGEAR).setComment("Settings related to Battlegear mods. Only shown with Battlegear or a fork of it installed.");
			enableReplaceShieldBar = config.getBoolean("Replace the shield stamina completely", CATEGORY_BATTLEGEAR, enableReplaceShieldBar, "Whether the mod should completely replace the shield stamina mechanic of the mod instead of just changing it if item use is disabled.");
			defaultStaminaGainForBattlegearShieldBash = config.getInt("Default stamina gain for shield bash", CATEGORY_BATTLEGEAR, defaultStaminaGainForBattlegearShieldBash, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when using the shield bash. Used if the shield doesn't have a custom attack value.");
			enableDualBowSupport = config.getBoolean("Dual bows support", CATEGORY_BATTLEGEAR, enableDualBowSupport, "Whether the stamina gain should be doubled whenever the player has a bow in both hands (meant for The Offhand Mod)");
		}

		if (config.hasChanged()) config.save();

		if (FMLCommonHandler.instance().getSide() != Side.CLIENT) return;

		// Makes pre-constructor changes to the GUI because Java is dumb.
		GuiConfig.array = new ArrayList<>();

		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_MECHANICS)));
		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_VISUALS)));
		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_FOOD)));

		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_STAGEAMOUNT)));
		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_NOSTAGE)));
		for (int i = 0; i < additionalStagesAmount; i += 1) GuiConfig.array.add(new ConfigElement(config.getCategory((CATEGORY_STAGE + (i + 1)))));

		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_VALUES)));
		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_REGENERATION)));
		GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_VANILLA)));
		if (Loader.isModLoaded("mw")) GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_MODERNWARFARE)));
		if (Loader.isModLoaded("battlegear2")) GuiConfig.array.add(new ConfigElement(config.getCategory(CATEGORY_BATTLEGEAR)));
	}

	public static Configuration getConfiguration() {
		return config;
	}
}