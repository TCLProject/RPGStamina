package net.tclproject.rpgstamina;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.item.Item;


public class Config {

	public static Configuration config;

	// Visuals category
	public static int staminaHUDXOffset = 0;
	public static int staminaHUDYOffset = 0;
	public static int staminaHUDRenderMode = 0;
	public static boolean staminaEmptyBar = true;
	public static boolean enableBlazeRodRender = true;

	// Mechanics category
	public static boolean enchantmentEnabled = true;
	public static boolean potionEnabled = true;
	public static boolean baubleEnabled = true;
	public static boolean commandEnabled = true;
	public static int enchantmentID = 125;
	public static int potionID = 30;

	// Food category
	public static boolean enableReplaceFood = false;
	public static int defaultFoodReplenishValue = 10;
	public static int defaultFoodHealthValue = 0;
	public static String[] customFoodValues = new String[0];
	public static HashMap<Item, Integer> internalFoodStaminaDict = new HashMap<>();
	public static HashMap<Item, Integer> internalFoodHealthDict = new HashMap<>();

	// Punishment stages
	public static class StageSettings {
		public String stageMaximumValue = "disabled";
		public boolean preventFoodEating = false;
		public boolean preventJump = false;
		public boolean preventSprint = false;
		public boolean preventAttack = false;
		public boolean useWhitelistForItemUseList = false;
		public String[] itemUseStrings = new String[0];
		public ArrayList<Item> internalItemUseList = new ArrayList<>();

		public int realValue = -1;
		public String[] potionEffects = new String[0];

		public StageSettings(String stageMaximumValue, boolean preventFoodEating, boolean preventJump, boolean preventSprint, boolean preventAttack, boolean useWhitelistForItemUseList, String[] itemUseStrings, String[] potionEffects) {
			this.stageMaximumValue = stageMaximumValue;
			this.preventFoodEating = preventFoodEating;
			this.preventJump = preventJump;
			this.preventSprint = preventSprint;
			this.preventAttack = preventAttack;
			this.useWhitelistForItemUseList = useWhitelistForItemUseList;

			this.itemUseStrings = itemUseStrings;
			for (String s : this.itemUseStrings) {
				String[] splittedItem = s.split(":");
				if (splittedItem.length >= 2) this.internalItemUseList.add(GameRegistry.findItem(splittedItem[0], splittedItem[1]));
			}

			//this.internalItemUseList = internalItemUseList;
			this.potionEffects = potionEffects;
		}

		public StageSettings() {}
	}

	public static int additionalStagesAmount = 2;
	public static StageSettings[] stageList;
	public static StageSettings[] defaultStageValues = new StageSettings[]{
			new StageSettings("disabled", false, false, false, false, false, new String[0], new String[0]), // STAGE 0 (No stage)
			new StageSettings(),
			new StageSettings()
	};


	// Values category
	public static int defaultMaxStamina = 200;
	public static int defaultStamina = 200;
	public static int staminaCapacityLevelUPPerEXPLevel = 25;
	public static int defaultStaminaGainForNaturalRegen = 3;
	public static int staminaAmountAfterDeath = -1;
	public static int delayBeforeNaturalRegen = 12;

	// Vanilla gains
	public static int staminaGainForJump = -3;
	public static int staminaGainForSprintJump = -3;
	public static int staminaGainForSprint = -10;
	public static int defaultStaminaGainForItemUse = -10;
	public static int defaultStaminaGainForAttack = -5;
	public static String[] customItemValues = new String[0];
	public static HashMap<Item, Integer> internalItemAttackDict = new HashMap<>();
	public static HashMap<Item, Integer> internalItemUseDict = new HashMap<>();

	// Modern Warfare 2 integration
	public static int staminaGainForMWGun = 0;

	// SmartMoving integration
	public static int staminaGainForSMCrawl = 0;
	public static int staminaGainForSMDiveJump = 0;
	public static int staminaGainForSMClimb = 0;

	// Battlegear integration
	public static boolean enableReplaceShieldBar = false;
	public static int defaultStaminaGainForBattlegearShieldBash = -15;


	// Category names
	public static String CATEGORY_VISUALS = "Visuals";
	public static String CATEGORY_MECHANICS = "Mechanics";
	public static String CATEGORY_FOOD = "Food";
	public static String CATEGORY_NOSTAGE = "Stages: No Stage";
	public static String CATEGORY_STAGEAMOUNT = "Stages: Amount of stages";
	public static String CATEGORY_STAGE = "Stages: Stage ";
	public static String CATEGORY_VALUES = "Values: Various settings";
	public static String CATEGORY_VANILLA = "Values: Vanilla actions";
	public static String CATEGORY_SMARTMOVING = "Values (Mod Integration): SmartMoving";
	public static String CATEGORY_MODERNWARFARE = "Values (Mod Integration): Modern Warfare 2";
	public static String CATEGORY_BATTLEGEAR = "Values (Mod integration): Battlegear 2 / The Offhand Mod";


	public static void init(String configDir, FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new Config());
		
		if (config == null) config = new Configuration( new File(configDir + "/" + "rpgstamina.cfg") );
	}

	public static void postInit() {
		loadConfiguration();
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


	public static void loadConfiguration() {
	// This function is responsible for updating the internal variables based on what is set in the config file.

		// Visuals category
		staminaHUDXOffset = config.getInt("HUD Position X Offset", CATEGORY_VISUALS, staminaHUDXOffset, -10000, 1000, "By how much the render of the stamina bar is offset left/right. For times it conflicts with other mods' bars positions.");
		staminaHUDYOffset = config.getInt("HUD Position Y Offset", CATEGORY_VISUALS, staminaHUDYOffset, -10000, 1000, "By how much the render of the stamina bar is offset up/down. For times it conflicts with other mods' bars positions.");
		staminaHUDRenderMode = config.getInt("HUD Render Mode", CATEGORY_VISUALS, staminaHUDRenderMode, 0, 9, "The HUD element where the stamina bar is attached. Only change this if you have compatibility issues.  0 = Auto, any other number is a specific hud element.");
		staminaEmptyBar = config.getBoolean("Empty stamina bar background", CATEGORY_VISUALS, staminaEmptyBar, "Option to disable the background of the stamina bar.");
		enableBlazeRodRender = config.getBoolean("Enable blaze rod render", CATEGORY_VISUALS, enableBlazeRodRender, "Makes the blaze rod render like a sword instead of like a normal item. This is the case in 1.8+.");


		// Mechanics category
		commandEnabled = config.getBoolean("Command enabled", CATEGORY_MECHANICS, commandEnabled, "Whether to enable the /stamina command.");
		enchantmentEnabled = config.getBoolean("Enchantment enabled", CATEGORY_MECHANICS, enchantmentEnabled, "Whether to enable the light feet enchantment.");
		potionEnabled = config.getBoolean("Potion enabled", CATEGORY_MECHANICS, potionEnabled, "Whether to enable the endurance potion.");
		baubleEnabled = config.getBoolean("Bauble enabled", CATEGORY_MECHANICS, baubleEnabled, "Whether to enable the endurance ring. (Requires the endurance potion to be enabled and the baubles mod installed).");
		enchantmentID = config.getInt("Light Feet Enchantment ID", CATEGORY_MECHANICS, enchantmentID, 0, Integer.MAX_VALUE, "ID of the Light Feet enchantment - change if conflicts. Unless using Enchantment ID Extender, maximum is 255.");
		potionID = config.getInt("Endurance Potion ID", CATEGORY_MECHANICS, potionID, 0, Integer.MAX_VALUE, "ID of the endurance potion - change if conflicts. Unless using Extended Potions, DragonAPI, Enchantment ID Extender or other extender mod, maximum is 31.");


		// Food category
		enableReplaceFood = config.getBoolean("Replace food completely", CATEGORY_FOOD, enableReplaceFood,  "Whether the mod should completely replace the current food mechanics in minecraft instead of adding to them. Use if you're bored of the Minecraft hunger system.");
		defaultFoodReplenishValue = config.getInt("Default food stamina gain", CATEGORY_FOOD, defaultFoodReplenishValue, Integer.MIN_VALUE, Integer.MAX_VALUE, "Defines how much stamina gets regenerated by eating food if it doesn't have a specific value defined.");
		defaultFoodHealthValue = config.getInt("Default food health replenish value", CATEGORY_FOOD, defaultFoodHealthValue, 0, Integer.MAX_VALUE, "Defines how much health gets regenerated by eating food if it doesn't have a specific value defined.");

		customFoodValues = config.getStringList("Custom food values", CATEGORY_FOOD, customFoodValues, "Custom item-specific values for food items. If not set, it will use the default value instead. Format: ModID:Item:Stamina:Health (the last one isn't required)");
		for (String s : customFoodValues) {
			String[] splitted = s.split(":");
			if (splitted.length >= 3) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[2], internalFoodStaminaDict);
			if (splitted.length >= 4) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[3], internalFoodHealthDict);
		}


		// Stage amount category
		additionalStagesAmount = config.getInt("Amount of additional stages", CATEGORY_STAGEAMOUNT, additionalStagesAmount, 0, Integer.MAX_VALUE, "The amount of additional stages to add along the \"no stage\" one (needs a restart to generate the config)");
		stageList = new StageSettings[additionalStagesAmount + 1];

		// Stages
		for (int i = 0; i < stageList.length; i += 1) {
			StageSettings defaultSetting = new StageSettings();
			if (i < defaultStageValues.length) defaultSetting = defaultStageValues[i];

			stageList[i] = new StageSettings();

			String endComment = "in this stage.";
			String category = CATEGORY_STAGE + i;
			if (i == 0) {
				endComment = "when not in a stage.";
				category = CATEGORY_NOSTAGE;
			}

			if (i > 0) stageList[i].stageMaximumValue = config.getString("Stage starting stamina amount", CATEGORY_STAGE + i, defaultSetting.stageMaximumValue, "Amount of stamina at or below which you enter this stage");

			stageList[i].preventJump = config.getBoolean("Prevent jump", category, defaultSetting.preventJump, "Whether the mod prevents you from jumping " + endComment);
			stageList[i].preventSprint = config.getBoolean("Prevent sprint", category, defaultSetting.preventSprint, "Whether the mod prevents you from sprinting " + endComment);
			stageList[i].preventAttack = config.getBoolean("Prevent attack", category, defaultSetting.preventAttack, "Whether the mod prevents you from attacking " + endComment);

			stageList[i].itemUseStrings = config.getStringList("Item usage list", category, defaultSetting.itemUseStrings, "List of items to block (or allow) usage of " + endComment + " Format: ModID:Item.");
			for (String s : stageList[i].itemUseStrings) {
				String[] splittedItem = s.split(":");
				if (splittedItem.length >= 2) stageList[i].internalItemUseList.add(GameRegistry.findItem(splittedItem[0], splittedItem[1]));
			}

			stageList[i].useWhitelistForItemUseList = config.getBoolean("Item usage whitelist instead of blacklist", category, defaultSetting.useWhitelistForItemUseList, "Whether the item use list should be a white or blacklist " + endComment);

			stageList[i].preventFoodEating = config.getBoolean("Prevent food eating", category, defaultSetting.preventFoodEating, "Whether the mod prevents you from eating food " + endComment + " (Requires food replacement to be enabled).");
			stageList[i].potionEffects = config.getStringList("Potion effects list", category, defaultSetting.potionEffects, "List of potion effects to apply " + endComment + " Format: PotionID:Amplifier:showParticles (only the PotionID is required)");
		}

		// Various settings category
		defaultMaxStamina = config.getInt("Default stamina capacity", CATEGORY_VALUES, defaultMaxStamina, 0, Integer.MAX_VALUE, "The amount of stamina capacity each player starts with.");
		defaultStamina = config.getInt("Default stamina amount", CATEGORY_VALUES, defaultStamina, 0, Integer.MAX_VALUE, "The amount of stamina each player starts with.");
		staminaAmountAfterDeath = config.getInt("Amount of stamina after death", CATEGORY_VALUES, staminaAmountAfterDeath, -2, Integer.MAX_VALUE, "The amount of stamina the player will have after dying and respawning. -2: Keep previous stamina amount, -1: Have maximum stamina, X: Have X stamina");
		defaultStaminaGainForNaturalRegen = config.getInt("Default natural regeneration gain", CATEGORY_VALUES, defaultStaminaGainForNaturalRegen, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for every second of not gaining (or losing) stamina through other means.");
		delayBeforeNaturalRegen = config.getInt("Default natural regeneration delay", CATEGORY_VALUES, delayBeforeNaturalRegen, 1, Integer.MAX_VALUE, "The amount of time in seconds the player would have to not have gained (or lost) stamina to let the natural regen begin.");
		staminaCapacityLevelUPPerEXPLevel = config.getInt("Stamina capacity level up with each XP level", CATEGORY_VALUES, staminaCapacityLevelUPPerEXPLevel, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina capacity a player gains (or loses) for each level of xp.");

		// Vanilla actions category
		staminaGainForJump = config.getInt("Jump gain", CATEGORY_VANILLA, staminaGainForJump, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when jumping (without sprinting).");
		staminaGainForSprint = config.getInt("Sprint gain", CATEGORY_VANILLA, staminaGainForSprint, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for each second of sprinting.");
		staminaGainForSprintJump = config.getInt("Sprint jump gain", CATEGORY_VANILLA, staminaGainForSprint, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when jumping while sprinting.");
		defaultStaminaGainForItemUse = config.getInt("Default item use gain", CATEGORY_VANILLA, defaultStaminaGainForItemUse, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) for each second of using an item.");
		defaultStaminaGainForAttack = config.getInt("Default attack gain", CATEGORY_VANILLA, defaultStaminaGainForAttack, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when attacking an entity.");

		customItemValues = config.getStringList("Custom item gains", CATEGORY_VANILLA, customItemValues, "Custom item-specific values for item usage and attack gains. If not set, it will use the default value instead. Format: ModID:Item:UseGain:AttackGain (The ModID,Item and UseGain values are required)");
		for (String s : customItemValues) {
			String[] splitted = s.split(":");
			if (splitted.length >= 3) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[2], internalItemUseDict);
			if (splitted.length >= 4) mapItem(splitted[0] + ":" + splitted[1] + ":" + splitted[3], internalItemAttackDict);
		}

		// SmartMoving integration category
		// This category only gets added if the SmartMoving mod is present.
		if (Loader.isModLoaded("SmartMoving")) {
			staminaGainForSMCrawl = config.getInt("Floor crawl gain", CATEGORY_SMARTMOVING, -20, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) each second when crawling (laying on the floor).");
			staminaGainForSMDiveJump = config.getInt("Dive jump gain", CATEGORY_SMARTMOVING, -3, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) each second when diving towards the ground.");
			staminaGainForSMClimb = config.getInt("Climb gain", CATEGORY_SMARTMOVING, -3, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) each second when climbing a block.");

			// TODO: A lot more configs for it
		}

		// Modern warfare integration category
		// This category only gets added if the ModernWarfare mod is present.
		if (Loader.isModLoaded("mw")) {
			staminaGainForMWGun = config.getInt("Shoot Modern Warfare gun gain", CATEGORY_MODERNWARFARE, -5, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when shooting a gun.");
		}

		// Battlegear 2 integration category
		// This category only gets added if the Battlegear2 mod is present.
		if (Loader.isModLoaded("battlegear2")) {
			enableReplaceShieldBar = config.getBoolean("Replace the shield stamina completely", CATEGORY_BATTLEGEAR, enableReplaceShieldBar,  "Whether the mod should completely replace the shield stamina mechanic of the mod instead of just changing it if item use is disabled.");
			defaultStaminaGainForBattlegearShieldBash  = config.getInt("Default stamina gain for shield bash", CATEGORY_BATTLEGEAR, defaultStaminaGainForBattlegearShieldBash, Integer.MIN_VALUE, Integer.MAX_VALUE, "The amount of stamina a player gains (or loses) when using the shield bash. Used if the shield doesn't have a custom attack value.");
		}

		if (config.hasChanged()) config.save();
	}


	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase("mysteriumpatches")) loadConfiguration();
	}
	
	public static Configuration getConfiguration() {
		return config;
	}
}