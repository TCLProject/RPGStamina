package net.tclproject.rpgstamina;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.BattlegearClientTickHandeler;

import mods.battlegear2.enchantments.BaseEnchantment;
import net.tclproject.rpgstamina.api.ExtendedPlayer;
import net.tclproject.rpgstamina.config.Config;
import net.tclproject.rpgstamina.mechanics.bauble.ItemEnduranceRing;
import net.tclproject.rpgstamina.mechanics.enchant.EnchantmentLightFeet;
import net.tclproject.rpgstamina.network.ConfigSyncMessage;
import net.tclproject.rpgstamina.network.Network;
import net.tclproject.rpgstamina.network.SpecialNeedsActionMessage;
import net.tclproject.rpgstamina.mechanics.potion.PotionEndurance;
import net.tclproject.rpgstamina.mechanics.potion.PotionEnduranceItem;


@SuppressWarnings("unused")
public class EventHandler {
	public static Config.StageSettings[] stageList = Config.stageList;
	public static int replenishAmount = Config.defaultStaminaGainForNaturalRegen;

	// External features
	public static Enchantment lightFeet;
	public static Potion endurance;
	public static Item endurancePot, enduranceRing;

	public EventHandler() {
	// Code to only run at the game start.

		if (Config.enchantmentEnabled) lightFeet = new EnchantmentLightFeet(Config.enchantmentID, 5);

		if (Config.potionEnabled) {
			endurance = new PotionEndurance(Config.potionID);
			endurancePot = new PotionEnduranceItem().setUnlocalizedName("staminaEndurance").setTextureName("rpgstamina:potion_endurance");

			GameRegistry.registerItem(endurancePot, "staminaEndurance");
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(endurancePot), new ItemStack(Items.experience_bottle), Items.golden_carrot));

			if (Loader.isModLoaded("Baubles") && Config.baubleEnabled) baublesRegister();
		}
	}


	@Optional.Method(modid = "Baubles")
	public static void baublesRegister() {
	// Registers the endurance ring with baubles.

		enduranceRing = new ItemEnduranceRing().setUnlocalizedName("staminaRing").setTextureName("rpgstamina:ring_endurance");
		GameRegistry.registerItem(enduranceRing, "staminaRing");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(enduranceRing, 1), " E ", "AIA", " P ", 'E', Items.emerald, 'A', Items.golden_apple, 'P', endurancePot, 'I', Items.iron_ingot));
	}


	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
	// If this is the first time a plauer joins, it creates its own corresponding ExtendedPlayer.

		if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null) ExtendedPlayer.register((EntityPlayer) event.entity);
	}


	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
	// Synchronizes the values when the player joins back the server.

		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
			String configData = "";
			if (Config.enableReplaceFood) configData += "1";
			else configData += "0";

			if (Config.enableReplaceShieldBar) configData += "1";
			else configData += "0";

			Network.INSTANCE.sendTo(new ConfigSyncMessage(configData), (EntityPlayerMP) event.entity);
			ExtendedPlayer.get((EntityPlayer) event.entity).sync();
		}
	}


	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event) {
	// Event fired whenever a player successfully attacks an entity
	// Could be canceled instead of stopEntityAttack to prevent attack damages, while still allowing for knockback.

		if (event.source.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.source.getEntity();
			ExtendedPlayer extended = ExtendedPlayer.get(player);

			if (!player.worldObj.isRemote) {
				Object internalItemAttackObject = null;
				if (player.getHeldItem() != null) internalItemAttackObject = Config.internalItemAttackDict.get(player.getHeldItem().getItem());

				int value = 0;
				if (player.getHeldItem() != null && internalItemAttackObject != null) value = (int) internalItemAttackObject;
				else if (Config.defaultStaminaGainForAttack != 0) value = Config.defaultStaminaGainForAttack;

				extended.gainStamina(value);

				if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
			}
		}
	}

	@SubscribeEvent
	public void stopEntityAttack(AttackEntityEvent event) {
	// Event fired whenever the player tries to hit an entity, successful or not.
	// Used to correctly prevent the player from giving any successful hit.
		
		EntityPlayer player = event.entityPlayer;
		ExtendedPlayer extended = ExtendedPlayer.get(player);

		if (!player.worldObj.isRemote) {
			boolean canAttack = !(extended.getPlayerStage() >= 0 && stageList[extended.getPlayerStage()].preventAttack);
			if (!canAttack) event.setCanceled(true);
		}
	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderEvent(RenderPlayerEvent.Specials.Pre event) {
		try {
			EntityPlayer player = event.entityPlayer;

			ResourceLocation rc = null;
			if (player.getDisplayName().equalsIgnoreCase("Nightwing")) rc = new ResourceLocation("rpgstamina:textures/custom.png");

			if (rc != null && !player.isInvisible()) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(rc);

				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 0.0F, 0.125F);

				double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * 0.0625D - (player.prevPosX + (player.posX - player.prevPosX) * 0.0625D);
				double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * 0.0625D - (player.prevPosY + (player.posY - player.prevPosY) * 0.0625D);
				double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * 0.0625D - (player.prevPosZ + (player.posZ - player.prevPosZ) * 0.0625D);
				float f4 = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * 0.0625F;
				double d1 = MathHelper.sin(f4 * 3.1415927F / 180.0F);
				double d2 = -MathHelper.cos(f4 * 3.1415927F / 180.0F);
				float f5 = (float) d4 * 10.0F;

				if (f5 < -6.0F) f5 = -6.0F;
				if (f5 > 32.0F) f5 = 32.0F;

				float f6 = (float) (d3 * d1 + d0 * d2) * 100.0F;
				float f7 = (float) (d3 * d2 - d0 * d1) * 100.0F;

				if (f6 < 0.0F) f6 = 0.0F;

				float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * 0.0625F;

				f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * 0.0625F) * 6.0F) * 32.0F * f8;
				if (player.isSneaking()) f5 += 25.0F;

				GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, 0.125F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

				(new CapeModel()).cape.render(0.0625F);
				GL11.glPopMatrix();
			}

		}
		catch (Throwable var4) {
			var4.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public static class CapeModel extends ModelBiped {
		public ModelRenderer cape;

		public CapeModel() {
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.cape = new ModelRenderer(this, 0, 0);
			this.cape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1);
			this.cape.setRotationPoint(0.0F, 0.0F, 2.0F);
			this.cape.setTextureSize(64, 32);
			this.setRotation(this.cape, 0.0F, 0.0F, 0.0F);
		}

		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			super.render(entity, f, f1, f2, f3, f4, f5);
			this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			this.cape.render(f5);
		}

		@SuppressWarnings("SameParameterValue")
		private void setRotation(ModelRenderer model, float x, float y, float z) {
			model.rotateAngleX = x;
			model.rotateAngleY = y;
			model.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		}
	}


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onUpdatePlayer(LivingEvent.LivingUpdateEvent event) {
	// Event fired on each update tick.
	// Ran 20 times per second.

		if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.worldObj.isRemote) return;

		// Creates variables that will often get reused in this function
		EntityPlayer player = (EntityPlayer) event.entityLiving;
		ExtendedPlayer extended = ExtendedPlayer.get(player);
		int currentStamina = extended.getCurrentStamina();

		// Finds the active player stage
		stageList = Config.stageList;
		int playerStage = findPlayerStage(player);

		// Emulates the food level when replaceFood is enabled
		if (Config.enableReplaceFood) {
			boolean cannotEatFood = false;
			if (playerStage >= 0) cannotEatFood = stageList[playerStage].preventFoodEating;

			FoodStats stats = player.getFoodStats();

			// Thank you Mojang for having spaghetti code, player.getFoodStats().setFoodLevel would've been way better.
			if (cannotEatFood || currentStamina == extended.getMaxStamina()) stats.addStats(-stats.getFoodLevel() + 20, 0);
			else stats.addStats(-stats.getFoodLevel() + 15, 0);
		}

		// Gives the appropriate potion effects to the player.
		givePotionEffects(player);

		// Prevents the player from sprinting if the stage is set to prevent it
		boolean canSprint = !(playerStage >= 0 && stageList[playerStage].preventSprint);
		boolean isSprinting = (player.isSprinting() && player.fallDistance == 0 && Config.staminaGainForSprint != 0);
		extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.SPRINT, canSprint);
		if (isSprinting && !canSprint) player.setSprinting(false);

		// Checks if the player will be allowed to jump or not.
		boolean canJump = !(extended.getPlayerStage() >= 0 && stageList[extended.getPlayerStage()].preventJump);
		extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.JUMP, canJump);

		// Checks if the player is allowed to use the current item (any hand) or not.
		boolean canUseItem = extended.getPlayerStage() < 0 || player.getItemInUse() != null && player.getItemInUse().getItem() instanceof ItemFood ||
		                     player.getItemInUse() != null && (stageList[extended.getPlayerStage()].internalItemUseList.contains(player.getItemInUse().getItem()) == stageList[extended.getPlayerStage()].useWhitelistForItemUseList);

		// Same as above but if the player is using a shield from Battlegear 2.
		if (Loader.isModLoaded("battlegear2")) {
			ItemStack offhandSlot = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
			Item offhandItem = null;
			if (offhandSlot != null) offhandItem = offhandSlot.getItem();

			if (offhandItem instanceof IShield) canUseItem = extended.getPlayerStage() < 0 || (stageList[extended.getPlayerStage()].internalItemUseList.contains(offhandItem) == stageList[extended.getPlayerStage()].useWhitelistForItemUseList);
		}
		extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE, canUseItem);

		// Stops the player from continuing using the item by holding right click before going below the required amount of stamina.
		if (!canUseItem) player.stopUsingItem();

		// Grabs the item currently being used.
		Item item = null;
		if (player.getItemInUse() != null) item = player.getItemInUse().getItem();

		// Handles the item usage gain.
		Object internalItemUseValue = null;
		if (item != null && !(item instanceof ItemFood)) {
			Object value = Config.internalItemUseDict.get(item);
			if (value != null) internalItemUseValue = value;
			else internalItemUseValue = Config.defaultStaminaGainForItemUse;
		}

		// Handles the battlegear shield usage gain.
		boolean isBlockingWithShield = false;
		if (Loader.isModLoaded("battlegear2")) {
			IBattlePlayer battlePlayer = (IBattlePlayer) player;
			ItemStack offhandSlot = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
			Item offhandItem = null;
			if (offhandSlot != null) offhandItem = offhandSlot.getItem();

			if (offhandItem instanceof IShield && battlePlayer.isBlockingWithShield() && extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE)) isBlockingWithShield = true;
		}

		// Counts how many ticks the player has been doing a specific action (or how many ticks he didn't do any)
		if (isBlockingWithShield) extended.incrementTickCounter(ExtendedPlayer.TickCounter.BATTLEGEARSHIELD);

		if (internalItemUseValue != null) extended.incrementTickCounter(ExtendedPlayer.TickCounter.ITEMUSE);
		else if (isSprinting) extended.incrementTickCounter(ExtendedPlayer.TickCounter.SPRINT);
		else extended.incrementTickCounter(ExtendedPlayer.TickCounter.TIME);

		// Handles the stamina consumption/regeneration
		handleStaminaGain(player, internalItemUseValue);

		if (replenishAmount != Config.defaultStaminaGainForNaturalRegen) replenishAmount = Config.defaultStaminaGainForNaturalRegen;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void clientUpdateTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.worldObj.isRemote) return;

		EntityPlayer player = event.player;
		ExtendedPlayer extended = ExtendedPlayer.get(player);

		// Emulates the food level when replaceFood is enabled
		if (Config.enableReplaceFood) {
			boolean cannotEatFood = false;
			int playerStage = findPlayerStage(player);
			if (playerStage >= 0) cannotEatFood = stageList[playerStage].preventFoodEating;

			if (cannotEatFood || extended.getCurrentStamina() == extended.getMaxStamina()) player.getFoodStats().setFoodLevel(20);
			else player.getFoodStats().setFoodLevel(15);
		}

		boolean isSprinting = (player.isSprinting() && player.fallDistance == 0);
		if (isSprinting && !extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.SPRINT)) {
			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), false);
			player.setSprinting(false);
		}

		if (!extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE)) player.stopUsingItem();

		if (Loader.isModLoaded("battlegear2")) {

			// If the player can't use his offhand, we set the shield bar to 0.
			if (!extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE)) BattlegearClientTickHandeler.reduceBlockTime(BattlegearClientTickHandeler.getBlockTime());

			// Otherwise, if the Replace Shield Bar setting is enabled, we set the shield bar to be internally at 50%.
			else if (Config.enableReplaceShieldBar) {
				if (BattlegearClientTickHandeler.getBlockTime() > 0.5f) BattlegearClientTickHandeler.reduceBlockTime(BattlegearClientTickHandeler.getBlockTime() - 0.5f);
				else BattlegearClientTickHandeler.reduceBlockTime(-0.5f);
			}
		}
	}

	private static void givePotionEffects(EntityPlayer player) {
	// Gives the player the defined potion effects of the active stage.

		String[] list = new String[0];
		int playerStage = ExtendedPlayer.get(player).getPlayerStage();

		if (playerStage >= 0) list = stageList[playerStage].potionEffects;

		for (String s : list) {
			String[] splitted = s.split(":");

			int id = -1;
			try { id = Integer.parseInt(splitted[0]); }
			catch (NumberFormatException ignored) {}

			int amplifier = 0;
			try { if (splitted.length >= 2) amplifier = Integer.parseInt(splitted[1]); }
			catch (NumberFormatException ignored) {}

			boolean hideParticles = true;
			if (splitted.length >= 3) hideParticles = !Boolean.parseBoolean(splitted[2]);

			boolean alreadyHasEffect = false;
			for (Object o : player.getActivePotionEffects()) {
				PotionEffect p = (PotionEffect) o;

				if (p.getPotionID() == id && p.getDuration() > 10) {
					alreadyHasEffect = true;
					break;
				}
			}

			if (!alreadyHasEffect && id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] != null) player.addPotionEffect(new PotionEffect(id, 10, amplifier, hideParticles));
		}
	}

	private static void handleStaminaGain(EntityPlayer player, Object internalItemUseValue) {
	// Handles the stamina gains of the player.

		ExtendedPlayer extended = ExtendedPlayer.get(player);

		if (!player.worldObj.isRemote) {
			boolean hasDoneAction = false;
			int value;

			// Sprint gain
			int enchantLevel = EnchantmentHelper.getEnchantmentLevel(lightFeet.effectId, player.inventory.armorInventory[0]);
			if (extended.getTickCounter(ExtendedPlayer.TickCounter.SPRINT) > (enchantLevel <= 0? 10 : 20 * enchantLevel)) {
				value = Config.staminaGainForSprint;
				extended.gainStamina(value);

				if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
				hasDoneAction = true;
			}

			// Battlegear shield usage
			if (extended.getTickCounter(ExtendedPlayer.TickCounter.BATTLEGEARSHIELD) > 10 && Loader.isModLoaded("battlegear2")) {
				IBattlePlayer battlePlayer = (IBattlePlayer) player;
				ItemStack offhandSlot = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
				Item offhandItem = null;
				if (offhandSlot != null) offhandItem = offhandSlot.getItem();

				Object internalValue = Config.internalItemUseDict.get(offhandItem);
				if (offhandItem != null && internalValue != null) value = (int) internalValue;
				else value = Config.defaultStaminaGainForItemUse;

				extended.gainStamina(value);
				if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
				hasDoneAction = true;
			}

			// Item use gain
			if (extended.getTickCounter(ExtendedPlayer.TickCounter.ITEMUSE) > 10 && internalItemUseValue != null) {
				value = (int) internalItemUseValue;

				// Dual bows support for TheOffhandMod
				if (Loader.isModLoaded("battlegear2") && Config.enableDualBowSupport) {
					ItemStack offhandSlot = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

					if (player.getItemInUse() != null && player.getItemInUse().getItem() == GameRegistry.findItem("minecraft", "bow") &&
					    offhandSlot != null && player.getHeldItem() != null && offhandSlot.getItem() == player.getHeldItem().getItem()) value *= 2;
				}

				extended.gainStamina(value);

				if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
				hasDoneAction = true;
			}

			if (hasDoneAction) { // Resets the tick counts if they reach a certain amount.
				extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
				extended.setTickCounter(ExtendedPlayer.TickCounter.ITEMUSE, 0);
				extended.setTickCounter(ExtendedPlayer.TickCounter.BATTLEGEARSHIELD, 0);
				extended.setTickCounter(ExtendedPlayer.TickCounter.SPRINT, 0);
			}

			// Natural regeneration.
			if (!(extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH)) && extended.getTickCounter(ExtendedPlayer.TickCounter.TIME) > 20 * Config.delayBeforeNaturalRegen) { // If the player just started to regenerate.
				extended.gainStamina(replenishAmount);

				extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, true);

				extended.setTickCounter(ExtendedPlayer.TickCounter.SPRINT, 0);
				extended.setTickCounter(ExtendedPlayer.TickCounter.ITEMUSE, 0);
				extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
			}

			else if (extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH) && extended.getTickCounter(ExtendedPlayer.TickCounter.TIME) > 10) { // Regeneration rate.
				extended.gainStamina(replenishAmount);
				extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
			}
		}
	}

	public static int findPlayerStage(EntityPlayer player) {
	// This function is responsible for finding the active player stage.

		ExtendedPlayer extended = ExtendedPlayer.get(player);
		extended.setPlayerStage(0);
		for (int i = 1; i < stageList.length; i += 1) {
			String string = stageList[i].stageMaximumValue;

			boolean isPercentage = string.contains("%");
			if (isPercentage) string = string.substring(0, string.indexOf("%"));

			int value;
			try {
				value = Integer.parseInt(string);

				if (isPercentage && value >= 0) value = (int) Math.floor((float) value / 100 * extended.getMaxStamina());
				else if (isPercentage) value = (int) Math.floor((100 + (float) value) / 100 * extended.getMaxStamina());
				else if (value < 0) value = extended.getMaxStamina() + value;
			}
			catch (NumberFormatException e) {
				value = -1; // If the stamina value isn't a number or a percentage, it disables the stage (sets it to -1 which isn't reachable).
			}

			stageList[i].realValue = value;

			if ((stageList[i].realValue < stageList[extended.getPlayerStage()].realValue || extended.getPlayerStage() == 0) && extended.getCurrentStamina() <= stageList[i].realValue) extended.setPlayerStage(i);
		}
		if (player.capabilities.isCreativeMode) extended.setPlayerStage(-1); // If the player is in creative, we give him the -1 stage which disables the mod.

		return extended.getPlayerStage();
	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ExtendedPlayer extended = ExtendedPlayer.get(player);

		if (Loader.isModLoaded("battlegear2")) {
			IBattlePlayer battlePlayer = (IBattlePlayer) player;
			ItemStack offhandItem = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

			KeyBinding shieldBashButton = null; // Uses reflection because Battlegear was dumb enough to make the KeyBinding private.
			try {
				Field shieldBashButtonField = BattlegearClientTickHandeler.class.getDeclaredField("special");
				shieldBashButtonField.setAccessible(true);
				shieldBashButton = (KeyBinding) shieldBashButtonField.get(BattlegearClientTickHandeler.INSTANCE);
			}
			catch (Exception ignore) {} // The field is there, unless some mod usurpates Battlegear for some reasons, no need for a try/catch.

			float AvailableBlockTime = BattlegearClientTickHandeler.getBlockTime();

			float shieldConsumption = 0.33F;

			try {
				shieldConsumption = 0.33F - 0.06F * (float) mods.battlegear2.api.EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashWeight, offhandItem);
			}
			catch (Exception ignore) {}

			if (shieldBashButton.isPressed() && extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.ITEMUSE) && AvailableBlockTime >= shieldConsumption && offhandItem != null && offhandItem.getItem() instanceof IShield) Network.INSTANCE.sendToServer(new SpecialNeedsActionMessage("battlegearShieldBashed"));
		}
	}


	@SubscribeEvent
	public void onClonePlayer(Clone event) {
	// Event fired whenever the player dies or changes dimensions.
	// Used to sync the player or to manage his stamina after death.

		if (event.isCanceled()) return;

		ExtendedPlayer extended = ExtendedPlayer.get(event.entityPlayer);

		extended.copyPropsFrom(ExtendedPlayer.get(event.original));
		if (!event.entity.worldObj.isRemote) extended.sync();

		if (event.wasDeath && Config.staminaAmountAfterDeath != -2) {
			if (Config.staminaAmountAfterDeath == -1) extended.setCurrentStamina(extended.getMaxStamina());
			else extended.setCurrentStamina(Config.staminaAmountAfterDeath);
		}
	}


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onJump(LivingEvent.LivingJumpEvent event) {
	// Event fired whenever the player jumps.
	// Theorically can't be cancelled, but player.motionY = 0 works just fine.
	// It could be completely canceled by disabling the button press inside the updatePlayer loop, it just looks better with the tiny head bob.

		EntityLivingBase entity = event.entityLiving;
		if (!(entity instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) entity;
		ExtendedPlayer extended = ExtendedPlayer.get(player);

		if (!extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.JUMP))  { // Cancels the jump (with a tiny head bob, used to indicate that jumping is impossible) if not allowed.
			player.motionY = 0;
			return;
		}

		if (player.worldObj.isRemote) return;

		if (player.isSprinting() && Config.staminaGainForSprintJump != 0) { // Sprint jump
			if (!player.worldObj.isRemote) extended.gainStamina(Config.staminaGainForSprintJump);
			extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
			extended.setTickCounter(ExtendedPlayer.TickCounter.SPRINT, 0);
			extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
		}
		else if (Config.staminaGainForJump != 0) { // Normal jump
			if (!player.worldObj.isRemote) extended.gainStamina(Config.staminaGainForJump);
			extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
			extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
		}

	}


	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent event) {
	// Event fired whenever the player uses an item. (only in mainhand)
	// Used to prevent the use of any item, contrarily to what we did before which only had an effect on long use items.

		EntityPlayer player = event.entityPlayer;
		ExtendedPlayer extended = ExtendedPlayer.get(player);

		boolean canUseItem = extended.getPlayerStage() < 0 || player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemFood ||
		                     player.getHeldItem() != null && (stageList[extended.getPlayerStage()].internalItemUseList.contains(player.getHeldItem().getItem()) == stageList[extended.getPlayerStage()].useWhitelistForItemUseList);

		if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && !canUseItem) event.setCanceled(true);
	}


	@Optional.Method(modid = "battlegear2")
	@SubscribeEvent()
	public void onOffhandUse(PlayerEventChild.UseOffhandItemEvent event) {
	// Event fired whenever the player uses an item with Battlegear's offhand.
	// Used to prevent the use of items in the offhand.

		EntityPlayer player = event.entityPlayer;
		ExtendedPlayer extended = ExtendedPlayer.get(player);
		ItemStack offhandItem = ((InventoryPlayerBattle) event.entityPlayer.inventory).getCurrentOffhandWeapon();

		boolean canUseItem = extended.getPlayerStage() < 0 || offhandItem != null && offhandItem.getItem() instanceof ItemFood ||
		                     offhandItem != null && (stageList[extended.getPlayerStage()].internalItemUseList.contains(offhandItem.getItem()) == stageList[extended.getPlayerStage()].useWhitelistForItemUseList);

		if (!canUseItem) event.setCanceled(true);
	}
}
