package net.tclproject.rpgstamina.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.tclproject.mysteriumlib.network.MagicNetwork;
import net.tclproject.rpgstamina.Config;
import net.tclproject.rpgstamina.packets.StaminaRefreshClient;

public class ExtendedPlayer implements IExtendedEntityProperties {
	
	public final static String EXT_PROP_NAME = "StaminaExtendedPlayer";
	
	// I always include the entity to which the properties belong for easy access
	// It's final because we won't be changing which player it is
	private final EntityPlayer player;

	// Declare other variables you want to add here
	public int currentStamina, maxStamina, playerStage;
	
	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.maxStamina = Config.defaultMaxStamina;
		this.currentStamina = Config.defaultStamina;
	}
	
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
	}
	
	public int getMaxStamina() {
		int value = maxStamina + (this.player.experienceLevel * Config.staminaCapacityLevelUPPerEXPLevel) + (this.player.isPotionActive(StEventHandler.endurance)? 250 : 0);
		if (value < 0) return 0;
		else return value;
	}
	
	/**
	 * Returns ExtendedPlayer properties for player
	 */
	public static final ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// We need to create a new tag compound that will save everything for our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();
		
		// We only have 2 variables currently; save them both to the new tag
		properties.setInteger("CurrentStamina", this.currentStamina);
		properties.setInteger("MaxStamina", this.maxStamina);
		
		// Now add our custom tag to the player's tag with a unique name (our property's name)
		// This will allow you to save multiple types of properties and distinguish between them
		// If you only have one type, it isn't as important, but it will still avoid conflicts between
		// your tag names and vanilla tag names. For instance, if you add some "Items" tag,
		// that will conflict with vanilla. Not good. So just use a unique tag name.
		compound.setTag(EXT_PROP_NAME, properties);
	}

	// Load whatever data you saved
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// Here we fetch the unique tag compound we set for this class of Extended Properties
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);

		// You cannot have less max stamina than you already have
		if (this.maxStamina > properties.getInteger("MaxStamina")) return;

		this.currentStamina = properties.getInteger("CurrentStamina");
		this.maxStamina = properties.getInteger("MaxStamina");
		this.sync();
	}
	
	public void sync() {
		
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) { 
			
			NBTTagCompound properties = new NBTTagCompound();
			properties.setInteger("CurrentStamina", this.currentStamina);
			properties.setInteger("MaxStamina", this.maxStamina);
			
			if (((EntityPlayerMP)player).playerNetServerHandler != null) {
				MagicNetwork.dispatcher.sendTo(new StaminaRefreshClient(this.player), (EntityPlayerMP) this.player);
			}
		}
	}

	@Override
	public void init(Entity entity, World world) {}


	public void gainStamina(int amount) {
		if (this.player.capabilities.isCreativeMode) return;

		if (this.currentStamina + amount > this.getMaxStamina()) this.currentStamina = this.getMaxStamina();
		else if (this.currentStamina + amount < 0) this.currentStamina = 0;
		else this.currentStamina += amount;

		this.sync();
	}

	
	public void setCurrentStamina(int amount) {
		this.currentStamina = (amount < this.getMaxStamina() ? amount : this.getMaxStamina());
		this.sync();
	}

	/**
	 * Sets max mana to amount or 0 if amount is less than 0
	 */
	public void setMaxStamina(int amount) {
		this.maxStamina = (amount > 0 ? amount : 0);
		this.sync();
	}
	
	public void copyPropsFrom(ExtendedPlayer props) {
        this.maxStamina = props.maxStamina;
        this.currentStamina = props.currentStamina;
        sync();
	}
}
