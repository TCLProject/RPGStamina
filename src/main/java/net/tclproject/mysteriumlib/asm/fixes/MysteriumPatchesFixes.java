package net.tclproject.mysteriumlib.asm.fixes;

import java.util.function.BiFunction;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponFireAspect;
import com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider;

import cpw.mods.fml.common.Optional;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.rpgstamina.Config;
import net.tclproject.rpgstamina.handler.ExtendedPlayer;
import net.tclproject.rpgstamina.handler.StEventHandler;

public class MysteriumPatchesFixes {


	@Fix(insertOnExit=true)
	public static void onEaten(ItemFood food, ItemStack itemStack, World world, EntityPlayer player) {

		if (!world.isRemote) {
			if (Config.internalFoodStaminaDict.get(itemStack.getItem()) != null) ExtendedPlayer.get(player).gainStamina(Config.internalFoodStaminaDict.get(itemStack.getItem()));
			else ExtendedPlayer.get(player).gainStamina(Config.defaultFoodReplenishValue);

			if (Config.internalFoodHealthDict.get(itemStack.getItem()) != null) player.setHealth(player.getHealth() + Config.internalFoodHealthDict.get(itemStack.getItem()));
			else player.setHealth(player.getHealth() + Config.defaultFoodHealthValue);

			StEventHandler.canReplenish = true;
		}
	}

	@Optional.Method(modid = "battlegear2")
	@Fix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean renderBlockBar(BattlegearInGameGUI instance, int x, int y) {
		if (Config.enableReplaceShieldBar) return true;
		return false;
	}

	@Optional.Method(modid="mw")
	@Fix
	public static void serverFire(WeaponFireAspect as, EntityLivingBase player, ItemStack itemStack, BiFunction spawnEntityWith, boolean isBurst) {
	      if (itemStack.getItem() instanceof Weapon && player instanceof EntityPlayer) {
	         int currentServerAmmo = getAmmo(itemStack);
	         if (currentServerAmmo > 0) {
	        	 if (Config.staminaGainForMWGun != 0) {
	     			ExtendedPlayer.get((EntityPlayer)player).gainStamina(Config.staminaGainForMWGun);
	     			StEventHandler.canReplenish = false;
	     			StEventHandler.timeTickCounter = 0;
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
	        	 if (Config.staminaGainForMWGun > 0) {
	     			ExtendedPlayer.get((EntityPlayer)player).gainStamina(Config.staminaGainForMWGun);
	     			StEventHandler.canReplenish = false;
	     			StEventHandler.timeTickCounter = 0;
	     		}
	         }
	      }
	}
	
	@Optional.Method(modid="mw")
	public static int getAmmo(ItemStack itemStack) {
	      return itemStack != null && CompatibilityProvider.compatibility.getTagCompound(itemStack) != null ? CompatibilityProvider.compatibility.getTagCompound(itemStack).getInteger("Ammo") : 0;
	}

// EXAMPLE: (more examples in the official wiki)
	
      /**
       * Target: every time the window is resized, print the new size
       */
//      @Fix
//      @SideOnly(Side.CLIENT)
//      public static void resize(Minecraft mc, int x, int y) {
//          System.out.println("Resize, x=" + x + ", y=" + y);
//     }

}
