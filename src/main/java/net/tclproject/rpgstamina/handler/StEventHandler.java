package net.tclproject.rpgstamina.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.tclproject.rpgstamina.PatchesConfig;
import net.tclproject.rpgstamina.bauble.ItemEnduranceRing;
import net.tclproject.rpgstamina.enchant.EnchantmentLightFeet;
import net.tclproject.rpgstamina.potion.PotionEndurance;
import net.tclproject.rpgstamina.potion.PotionEnduranceItem;
import org.lwjgl.opengl.GL11;

public class StEventHandler {
   
   public static int replenishAmount = PatchesConfig.defaultStaminaRegen;
   public static boolean canReplenish, alreadyDone, potionApplied = false;
   public static int foodCooldown, ticksPassed, ticksPassed2, ticksPassed3;
   public static Enchantment lightFeet;
   public static Potion endurance;
   public static Item endurancePot, enduranceRing;

   public StEventHandler() {
		if (PatchesConfig.enchantmentEnabled) {
			lightFeet = new EnchantmentLightFeet(PatchesConfig.defaultEnchantmentID, 5);
		}
		if (PatchesConfig.potionEnabled) {
			endurance = new PotionEndurance(PatchesConfig.defaultPotionID);
			endurancePot = new PotionEnduranceItem().setUnlocalizedName("staminaEndurance").setTextureName("rpgstamina:potion_endurance");
			GameRegistry.registerItem(endurancePot, "staminaEndurance");
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(endurancePot), new ItemStack(Items.experience_bottle), Items.golden_carrot));

			if (Loader.isModLoaded("Baubles") && PatchesConfig.baubleEnabled) {
				baublesRegister();
			}
		}
   }

	@Optional.Method(modid="Baubles")
   public static void baublesRegister() {
	   enduranceRing = new ItemEnduranceRing().setUnlocalizedName("staminaRing").setTextureName("rpgstamina:ring_endurance");
	   GameRegistry.registerItem(enduranceRing, "staminaRing");
	   GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(enduranceRing, 1), new Object[] {" E ", "AIA", " P ", 'E', Items.emerald, 'A', Items.golden_apple, 'P', endurancePot, 'I', Items.iron_ingot}));
   }
   
   @SubscribeEvent
   public void onEntityConstructing(EntityConstructing event)
   {
	   if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
		   ExtendedPlayer.register((EntityPlayer) event.entity);
	   }
   }
   
   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event)
   {
       if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) ExtendedPlayer.get((EntityPlayer) event.entity).sync();
   }
   
   @SubscribeEvent
   public void onEntityAttack(AttackEntityEvent event) {
	   if (!event.entityPlayer.worldObj.isRemote) {
		   if (PatchesConfig.staminaDrainHit) ExtendedPlayer.get(event.entityPlayer).consumeStamina(PatchesConfig.defaultStaminaForHit);
	   }
   }

	ResourceLocation rc = new ResourceLocation("rpgstamina:textures/custom.png");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(
			priority = EventPriority.HIGHEST
	)
	public void onRenderEvent(RenderPlayerEvent.Specials.Pre event) {
		try {
			EntityPlayer player = event.entityPlayer;
			if ("Nlghtwing".equalsIgnoreCase(player.getDisplayName())) {
				this.render(player);
			}
		} catch (Throwable var4) {
			var4.printStackTrace();
		}

	}

	private void render(EntityPlayer player) {
		if (!player.isInvisible()) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(rc);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
			double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * 0.0625D - (player.prevPosX + (player.posX - player.prevPosX) * 0.0625D);
			double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * 0.0625D - (player.prevPosY + (player.posY - player.prevPosY) * 0.0625D);
			double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * 0.0625D - (player.prevPosZ + (player.posZ - player.prevPosZ) * 0.0625D);
			float f4 = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * 0.0625F;
			double d1 = (double) MathHelper.sin(f4 * 3.1415927F / 180.0F);
			double d2 = (double)(-MathHelper.cos(f4 * 3.1415927F / 180.0F));
			float f5 = (float)d4 * 10.0F;
			if (f5 < -6.0F) {
				f5 = -6.0F;
			}

			if (f5 > 32.0F) {
				f5 = 32.0F;
			}

			float f6 = (float)(d3 * d1 + d0 * d2) * 100.0F;
			float f7 = (float)(d3 * d2 - d0 * d1) * 100.0F;
			if (f6 < 0.0F) {
				f6 = 0.0F;
			}

			float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * 0.0625F;
			f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * 0.0625F) * 6.0F) * 32.0F * f8;
			if (player.isSneaking()) {
				f5 += 25.0F;
			}

			GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			getCapeModel().cape.render(0.0625F);
			GL11.glPopMatrix();
		}

	}

	@SideOnly(Side.CLIENT)
	private CapeModel getCapeModel() {
		return new CapeModel();
	}

	@SideOnly(Side.CLIENT)
	public class CapeModel extends ModelBiped {
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

		private void setRotation(ModelRenderer model, float x, float y, float z) {
			model.rotateAngleX = x;
			model.rotateAngleY = y;
			model.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		}
	}

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onUpdatePlayer(TickEvent.PlayerTickEvent event) {
	   System.out.println(ExtendedPlayer.get(event.player).currentStamina);
       if (PatchesConfig.enableReplaceFood && event.player.getFoodStats().getFoodLevel() != 19) {
    	   event.player.getFoodStats().setFoodLevel(19);
       }
       if (foodCooldown > 0) foodCooldown -= 1;
	   
	   if (event.player.isSprinting() && ExtendedPlayer.get(event.player).currentStamina == 0) { event.player.setSprinting(false); }
	   if (event.player.isInWater() && ExtendedPlayer.get(event.player).currentStamina == 0 && !event.player.isPotionActive(2)) { event.player.addPotionEffect(new PotionEffect(2, 5, 1, true)); potionApplied = true;}
	   if (event.player.isUsingItem() && event.player.getHeldItem() != null && !(event.player.getHeldItem().getItem() instanceof ItemFood) && ExtendedPlayer.get(event.player).currentStamina == 0) { event.player.stopUsingItem(); }
	   if (ExtendedPlayer.get(event.player).currentStamina <= 20 && !event.player.isPotionActive(2) ) {
		   event.player.addPotionEffect(new PotionEffect(2, 5, 1, true));
		   event.player.addPotionEffect(new PotionEffect(18, 5, 1, true));
		   potionApplied = true;
	   } else if (ExtendedPlayer.get(event.player).currentStamina > 20 && event.player.isPotionActive(2) && potionApplied) {
		   event.player.clearActivePotions();
		   event.player.removePotionEffect(2);
		   event.player.removePotionEffect(18);
		   if (event.player.worldObj.isRemote) {
			   event.player.removePotionEffectClient(2);
			   event.player.removePotionEffectClient(18);
		   }
		   potionApplied = false;
	   }
	   if (!alreadyDone && ExtendedPlayer.get(event.player).currentStamina <= 30 && event.player.attackTime > 0) {
	       event.player.attackTime += 5;
	       alreadyDone = true;
	   }
	   if (event.player.attackTime == 0) alreadyDone = false;

	   int e = EnchantmentHelper.getEnchantmentLevel(lightFeet.effectId, event.player.inventory.armorInventory[0]);

	   boolean isSprinting = (event.player.isSprinting() && PatchesConfig.staminaDrainSprint);
	   boolean usingItem = (PatchesConfig.staminaDrainUse && event.player.isUsingItem() && !(event.player.getHeldItem() != null && event.player.getHeldItem().getItem() instanceof ItemFood));

	   if (usingItem) {
	   	ticksPassed += 1;
	   } else if (isSprinting) {
		ticksPassed3 += 1;
		if (ticksPassed3 >= e*2) {
			ticksPassed += 1;
			ticksPassed3 = 0;
		}
	   } else {
		   ticksPassed2 += 1;
	   }

	   if (!event.player.worldObj.isRemote) {
		   if (ticksPassed > 60) {
			   ExtendedPlayer.get(event.player).consumeStamina(PatchesConfig.defaultStaminaForSprint);
			   if (!event.player.worldObj.isRemote) ExtendedPlayer.get(event.player).sync();
			   ticksPassed = 0;
			   ticksPassed2 = 0;
			   canReplenish = false;
		   }
		   if (!canReplenish && ticksPassed2 > 240) {
			   ExtendedPlayer.get(event.player).replenishStamina(replenishAmount);
			   canReplenish = true;
			   ticksPassed2 = 0;
		   } else if (canReplenish && ticksPassed2 > 60) {
			   ExtendedPlayer.get(event.player).replenishStamina(replenishAmount);
			   ticksPassed2 = 0;
		   }
	   }
	   
	   if (replenishAmount != PatchesConfig.defaultStaminaRegen) replenishAmount = PatchesConfig.defaultStaminaRegen;
   }
   
	@SubscribeEvent
	public void onClonePlayer(Clone event) {
		ExtendedPlayer.get(event.entityPlayer).copyPropsFrom(ExtendedPlayer.get(event.original));
		if (!event.entity.worldObj.isRemote) {
			ExtendedPlayer.get(event.entityPlayer).sync();
		}
	}
}
