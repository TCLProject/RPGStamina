package net.tclproject.rpgstamina.api;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.tclproject.rpgstamina.config.Config;
import net.tclproject.rpgstamina.EventHandler;
import net.tclproject.rpgstamina.network.Network;
import net.tclproject.rpgstamina.network.StaminaClientRefreshMessage;

@SuppressWarnings("unused")
public class ExtendedPlayer implements IExtendedEntityProperties {
// This class lets us add NBT properties to the player.

	// The NBT group name, should be unique to prevent conflicts with other mods and vanilla.
	public final static String NBT_GROUP_NAME = "RPGStaminaExtendedPlayer";

	// The player to which the properties belong to.
	// It's final because we won't be changing which player it is
	private final EntityPlayer player;

	// Player values
	private int currentStamina, maxStamina, playerStage;

	// Tick counters
	private int timeTickCounter, sprintTickCounter, itemUseTickCounter, battlegearShieldTickCounter;
	public enum TickCounter {
		TIME, SPRINT, ITEMUSE, BATTLEGEARSHIELD
	}

	// Player abilities
	private boolean canSprint, canJump, canUse, canReplenish;
	public enum PlayerAbility {
		SPRINT, JUMP, ITEMUSE, REPLENISH
	}


	@Override
	public void init(Entity entity, World world) {} // Isn't used by anything, just required to be there to implement IExtendedEntityProperties.

	/**
	Initialises the properties with the default values and links the properties to the player.

	@param player The player to link the properties to.
	*/
	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.maxStamina = Config.defaultMaxStamina;
		this.currentStamina = Config.defaultStamina;
		this.playerStage = 0;
		this.canSprint = true;
		this.canJump = true;
		this.canUse = true;
		this.canReplenish = true;
	}


	/**
	Copies the properties of another ExtendedPlayer.

	@param source The ExtendedPlayer to copy the properties from.
	*/
	public void copyPropsFrom(ExtendedPlayer source) {
		this.maxStamina = source.maxStamina;
		this.currentStamina = source.currentStamina;
		this.playerStage = source.playerStage;

		this.timeTickCounter = source.timeTickCounter;
		this.sprintTickCounter = source.sprintTickCounter;
		this.itemUseTickCounter = source.itemUseTickCounter;

		this.battlegearShieldTickCounter = source.battlegearShieldTickCounter;

		this.canSprint = source.canSprint;
		this.canJump = source.canJump;
		this.canUse = source.canUse;
		this.canReplenish = source.canReplenish;

		this.sync();
	}


	/**
	Registers the NBT group into the NBT of the player.

	@param player The player with whom the properties should be attached.
	*/
	public static void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.NBT_GROUP_NAME, new ExtendedPlayer(player));
	}


	/**
	Returns the associated ExtendedPlayer for a specific player.

	@param player The player associated with the ExtendedPlayer.
	@return The ExtendedPlayer instance with the values and methods correctly attached to the given player.
	*/
	public static ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getExtendedProperties(NBT_GROUP_NAME);
	}


	/**
	Saves the NBT data.

	@param compound The NBTTagCompound of the player, with all the player's NBT value.
	*/
	@Override
	public void saveNBTData(NBTTagCompound compound) {
	// Saves the NBT data.

		// We need to create a new tag compound that will save everything for our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();

		// Saves every value to the new tag
		properties.setInteger("CurrentStamina", this.currentStamina);
		properties.setInteger("MaxStamina", this.maxStamina);
		properties.setInteger("PlayerStage", this.playerStage);

		// Tick counters
		properties.setInteger("TimeTickCounter", this.timeTickCounter);
		properties.setInteger("SprintTickCounter", this.sprintTickCounter);
		properties.setInteger("ItemUseTickCounter", this.itemUseTickCounter);

		properties.setInteger("BattlegearOffhandUseTickCounter", this.battlegearShieldTickCounter);

		properties.setBoolean("CanSprint", this.canSprint);
		properties.setBoolean("CanJump", this.canJump);
		properties.setBoolean("CanUse", this.canUse);
		properties.setBoolean("CanReplenish", this.canReplenish);

		// Adds every NBT property inside an NBT folder (the other compound created above).
		// This ensures mod and vanilla compatibility to not accidentally overwrite an unrelated value.
		compound.setTag(NBT_GROUP_NAME, properties);
	}


	/**
	Loads the saved NBT values into the ExtendedPlayer object.

	@param compound The NBTTagCompound of the player, with all the player's NBT value.
	*/
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// Here we fetch the unique tag compound we set for this class of Extended Properties
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(NBT_GROUP_NAME);

		// Ensures we don't somehow have a higher maxStamina value than what's stored.
		if (this.maxStamina > properties.getInteger("MaxStamina")) return;

		// Sets our values to be the ones stored in the NBT.
		this.currentStamina = properties.getInteger("CurrentStamina");
		this.maxStamina = properties.getInteger("MaxStamina");
		this.playerStage = properties.getInteger("PlayerStage");

		this.timeTickCounter = properties.getInteger("TimeTickCounter");
		this.sprintTickCounter = properties.getInteger("SprintTickCounter");
		this.itemUseTickCounter = properties.getInteger("ItemUseTickCounter");

		this.battlegearShieldTickCounter = properties.getInteger("BattlegearOffhandUseTickCounter");

		this.canSprint = properties.getBoolean("CanSprint");
		this.canJump = properties.getBoolean("CanJump");
		this.canUse = properties.getBoolean("CanUse");
		this.canReplenish = properties.getBoolean("CanReplenish");

		this.sync();
	}


	/**
	Syncs the client values with the server's ones.
	*/
	public void sync() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			NBTTagCompound properties = new NBTTagCompound();

			// Ints
			properties.setInteger("CurrentStamina", this.currentStamina);
			properties.setInteger("MaxStamina", this.maxStamina);
			properties.setInteger("PlayerStage", this.playerStage);

			// Tick counters
			properties.setInteger("TimeTickCounter", this.timeTickCounter);
			properties.setInteger("SprintTickCounter", this.sprintTickCounter);
			properties.setInteger("ItemUseTickCounter", this.itemUseTickCounter);

			properties.setInteger("BattlegearOffhandUseTickCounter", this.battlegearShieldTickCounter);

			// Player abilities
			properties.setBoolean("CanSprint", this.canSprint);
			properties.setBoolean("CanJump", this.canJump);
			properties.setBoolean("CanUse", this.canUse);
			properties.setBoolean("CanReplenish", this.canReplenish);

			if (((EntityPlayerMP) player).playerNetServerHandler != null) Network.INSTANCE.sendTo(new StaminaClientRefreshMessage(this.player), (EntityPlayerMP) this.player);
		}
	}


	/**
	@return The current amount stamina of the given player.
	*/
	public int getCurrentStamina() {
		return this.currentStamina;
	}


	/**
	@return The maximum amount of stamina this specific player can have. Calculated based on the potion effect, XP level etc.
	*/
	public int getMaxStamina() {
		int value = this.maxStamina + (this.player.experienceLevel * Config.staminaCapacityLevelUPPerEXPLevel) + (this.player.isPotionActive(EventHandler.endurance)? 250 : 0);
		return Math.max(value, 0);
	}


	/**
	@return The current player stage (used by the server only).
	*/
	public int getPlayerStage() {
		return this.playerStage;
	}


	/**
	@param key The key of the specific ability to fetch.
	@return Whether the player is allowed to execute the given action.
	*/
	public boolean getPlayerAbility(PlayerAbility key) {
		switch (key) {
			case SPRINT: return this.canSprint;
			case JUMP: return this.canJump;
			case ITEMUSE: return this.canUse;
			case REPLENISH: return this.canReplenish;
		}

		return false;
	}


	/**
	@param key The key of the specific tick counter to fetch.
	@return The amount of server ticks the player has been doing a specific action.
	*/
	public int getTickCounter(TickCounter key) {
		switch (key) {
			case TIME: return this.timeTickCounter;
			case SPRINT: return this.sprintTickCounter;
			case ITEMUSE: return this.itemUseTickCounter;
			case BATTLEGEARSHIELD: return this.battlegearShieldTickCounter;
		}

		return -1;
	}


	/**
	Adds a given amount of stamina to the player.

	@param amount The amount of stamina to add. Can be negative to substract stamina instead.
	*/
	public void gainStamina(int amount) {
		if (this.player.capabilities.isCreativeMode) return;

		if (this.currentStamina + amount > this.getMaxStamina()) this.currentStamina = this.getMaxStamina();
		else if (this.currentStamina + amount < 0) this.currentStamina = 0;
		else this.currentStamina += amount;

		this.sync();
	}


	/**
	Sets the current stamina amount of the player.

	@param amount The amount of stamina the player should have. It cannot be below 0 or above the maxStamina.
	*/
	public void setCurrentStamina(int amount) {
		this.currentStamina = Math.max(0, Math.min(amount, this.getMaxStamina()));
		this.sync();
	}


	/**
	Sets the maximum amount of stamina the player can have.

	@param amount The maximum amount of stamina the player can have. Cannot be negative.
	*/
	public void setMaxStamina(int amount) {
		this.maxStamina = Math.max(amount, 0);
		this.sync();
	}


	/**
	Sets the active player stage. Only use this if you know what you're doing.

	@param value The stage ID of the playerStage. By convention, -1 is a disabled stage. 0-x is a specific stage.
	*/
	public void setPlayerStage(int value) {
		this.playerStage = value;
		this.sync();
	}


	/**
	Sets whether the player should be allowed or not to do a specific action.

	@param key The key of the specific ability to change.
	@param value The value to set it to.
	*/
	public void setPlayerAbility(PlayerAbility key, boolean value) {
		switch (key) {
			case SPRINT:
				this.canSprint = value;
				break;
			case JUMP:
				this.canJump = value;
				break;
			case ITEMUSE:
				this.canUse = value;
				break;
			case REPLENISH:
				this.canReplenish = value;
				break;
		}

		this.sync();
	}


	/**
	Increments a specific tick counter of the player.

	@param key The key of the specific tick counter to change.
	*/
	public void incrementTickCounter(TickCounter key) {
		switch (key) {
			case TIME:
				this.timeTickCounter += 1;
				break;

			case SPRINT:
				this.sprintTickCounter += 1;
				break;

			case ITEMUSE:
				this.itemUseTickCounter += 1;
				break;

			case BATTLEGEARSHIELD:
				this.battlegearShieldTickCounter += 1;
				break;
		}

		this.sync();
	}


	/**
	Sets a specific tick counter of the player to a given value.

	@param key The key of the specific tick counter to change.
	@param value The value to set it to. By convention, it shouldn't be negative.
	*/
	public void setTickCounter(TickCounter key, int value) {
		switch (key) {
			case TIME:
				this.timeTickCounter = value;
				break;

			case SPRINT:
				this.sprintTickCounter = value;
				break;

			case ITEMUSE:
				this.itemUseTickCounter = value;
				break;

			case BATTLEGEARSHIELD:
				this.battlegearShieldTickCounter = value;
				break;
		}

		this.sync();
	}
}