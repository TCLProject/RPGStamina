package net.tclproject.rpgstamina.mechanics.bauble;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.tclproject.rpgstamina.EventHandler;

public class ItemEnduranceRing extends Item implements IBauble {

    public ItemEnduranceRing() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!par2World.isRemote) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(par3EntityPlayer);

            for(int i = 0; i < baubles.getSizeInventory(); ++i) {
                if (baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, par1ItemStack)) {
                    baubles.setInventorySlotContents(i, par1ItemStack.copy());
                    if (!par3EntityPlayer.capabilities.isCreativeMode) {
                        par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, null);
                    }

                    this.onEquipped(par1ItemStack, par3EntityPlayer);
                    break;
                }
            }
        }

        return par1ItemStack;
    }

    public boolean hasEffect(ItemStack par1ItemStack, int a) {
        return true;
    }

    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }


    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (itemstack.getItemDamage() == 0 && !player.isPotionActive(EventHandler.endurance)) {
            player.addPotionEffect(new PotionEffect(EventHandler.endurance.id, 1800, 0, true));
        }
    }

    @Override
    public void onEquipped(ItemStack itemStack, EntityLivingBase player) {
        if (!player.worldObj.isRemote) {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 1.3F);
        }
    }

    @Override
    public void onUnequipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public boolean canEquip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }
}
