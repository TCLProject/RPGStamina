package net.tclproject.rpgstamina.fixes;

import net.minecraftforge.client.GuiIngameForge;
import java.util.function.BiFunction;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponFireAspect;
import com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider;

import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.rpgstamina.config.Config;
import net.tclproject.rpgstamina.api.ExtendedPlayer;


@SuppressWarnings("unused")
public class ModFixes {
	@Fix(insertOnExit=true)
	public static void onEaten(ItemFood food, ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			ExtendedPlayer extended = ExtendedPlayer.get(player);

			int value = Config.defaultFoodStaminaGain;
			if (Config.internalFoodStaminaDict.get(itemStack.getItem()) != null) value = Config.internalFoodStaminaDict.get(itemStack.getItem());

			extended.gainStamina(value);

			if (Config.internalFoodHealthDict.get(itemStack.getItem()) != null) player.setHealth(player.getHealth() + Config.internalFoodHealthDict.get(itemStack.getItem()));
			else player.setHealth(player.getHealth() + Config.defaultFoodHealthValue);

			if (value != 0) extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
		}
	}

	@Fix(returnSetting = EnumReturnSetting.ALWAYS)
	public static boolean canEat(EntityPlayer instance, boolean allowEatingWhenFull) {
		ExtendedPlayer extended = ExtendedPlayer.get(instance);

		return !instance.capabilities.disableDamage && extended.getPlayerAbility(ExtendedPlayer.PlayerAbility.EAT) && (allowEatingWhenFull || instance.getFoodStats().needFood() || extended.getCurrentStamina() < extended.getMaxStamina());
	}

	@Fix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean renderAir(GuiIngameForge instance, int width, int height) {
		if (!Config.allowOxygenBarOverlap && (Config.staminaEmptyBar || ExtendedPlayer.get(Minecraft.getMinecraft().thePlayer).getCurrentStamina() > 0)) GuiIngameForge.right_height += 10;

		return false;
	}

	@Optional.Method(modid = "battlegear2")
	@Fix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean renderBlockBar(BattlegearInGameGUI instance, int x, int y) {
		return Config.enableReplaceShieldBar;
	}


	@Optional.Method(modid="mw")
	@Fix
	public static void serverFire(WeaponFireAspect as, EntityLivingBase player, ItemStack itemStack, BiFunction spawnEntityWith, boolean isBurst) {
		if (itemStack.getItem() instanceof Weapon && player instanceof EntityPlayer) {
			int currentServerAmmo = getAmmo(itemStack);
			if (currentServerAmmo > 0) {

				if (Config.staminaGainForMWGun != 0) {
					ExtendedPlayer extended = ExtendedPlayer.get((EntityPlayer)player);
					extended.gainStamina(Config.staminaGainForMWGun);
					extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
					extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
	     		}
			}

		}
	}
	
	@Optional.Method(modid="mw")
	@Fix
	public static void serverFire(WeaponFireAspect as, EntityLivingBase player, ItemStack itemStack, BiFunction spawnEntityWith) {
		if (itemStack.getItem() instanceof Weapon && player instanceof EntityPlayer) {
			int currentServerAmmo = getAmmo(itemStack);
			if (currentServerAmmo > 0) {

				if (Config.staminaGainForMWGun != 0) {
					ExtendedPlayer extended = ExtendedPlayer.get((EntityPlayer)player);
	     			extended.gainStamina(Config.staminaGainForMWGun);
					extended.setPlayerAbility(ExtendedPlayer.PlayerAbility.REPLENISH, false);
					extended.setTickCounter(ExtendedPlayer.TickCounter.TIME, 0);
	     		}

			}
		}
	}
	
	@Optional.Method(modid="mw")
	public static int getAmmo(ItemStack itemStack) {
	      return itemStack != null && CompatibilityProvider.compatibility.getTagCompound(itemStack) != null ? CompatibilityProvider.compatibility.getTagCompound(itemStack).getInteger("Ammo") : 0;
	}
}
