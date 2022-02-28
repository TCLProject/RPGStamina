package net.tclproject.mysteriumlib.asm.fixes;

import java.util.function.BiFunction;

import com.vicmatskiv.weaponlib.Tags;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponFireAspect;
import com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider;

import cpw.mods.fml.common.Optional;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.rpgstamina.PatchesConfig;
import net.tclproject.rpgstamina.handler.ExtendedPlayer;
import net.tclproject.rpgstamina.handler.StEventHandler;

public class MysteriumPatchesFixes {

	@Fix(insertOnExit=true)
	public static void onEaten(ItemFood itmf, ItemStack itemstack, World p_77654_2_, EntityPlayer p_77654_3_)
    {
		StEventHandler.foodCooldown = ((ItemFood)itemstack.getItem()).func_150905_g(itemstack) * 50;
        StEventHandler.replenishAmount = Math.round(((ItemFood)itemstack.getItem()).func_150905_g(itemstack) * 2 * PatchesConfig.foodMultiplier);
        StEventHandler.canReplenish = true;
        ExtendedPlayer.get(p_77654_3_).replenishStamina(Math.round(((ItemFood)itemstack.getItem()).func_150905_g(itemstack) * 4 * PatchesConfig.foodMultiplier));
    }
	
	@Fix(returnSetting=EnumReturnSetting.ON_TRUE)
	public static boolean jump(EntityPlayer p) {
		if (PatchesConfig.preventJump && ExtendedPlayer.get(p).currentStamina == 0) {
			return true;
		}
		if (PatchesConfig.staminaDrainJump) {
			ExtendedPlayer.get(p).consumeStamina(PatchesConfig.defaultStaminaForJump);
			StEventHandler.canReplenish = false;
			StEventHandler.ticksPassed2 = 0;
		}
		return false;
	}
	
	@Optional.Method(modid="mw")
	@Fix
	public static void serverFire(WeaponFireAspect as, EntityLivingBase player, ItemStack itemStack, BiFunction spawnEntityWith, boolean isBurst) {
	      if (itemStack.getItem() instanceof Weapon && player instanceof EntityPlayer) {
	         int currentServerAmmo = getAmmo(itemStack);
	         if (currentServerAmmo > 0) {
	        	 if (PatchesConfig.staminaDrainGun) {
	     			ExtendedPlayer.get((EntityPlayer)player).consumeStamina(PatchesConfig.defaultStaminaForGun);
	     			StEventHandler.canReplenish = false;
	     			StEventHandler.ticksPassed2 = 0;
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
	        	 if (PatchesConfig.staminaDrainGun) {
	     			ExtendedPlayer.get((EntityPlayer)player).consumeStamina(PatchesConfig.defaultStaminaForGun);
	     			StEventHandler.canReplenish = false;
	     			StEventHandler.ticksPassed2 = 0;
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
