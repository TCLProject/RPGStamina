package net.tclproject.rpgstamina.mechanics.potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tclproject.rpgstamina.config.Config;

import java.util.List;

public class PotionEnduranceItem extends ItemFood {
    public PotionEnduranceItem()
    {
        super(32, 1, false);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setAlwaysEdible();
        this.setPotionEffect(Config.potionID, 90, 0, 1f);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.drink;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack p_77636_1_)
    {
        return true;
    }

    public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_)
    {
        if (!p_77654_3_.capabilities.isCreativeMode) {
            --p_77654_1_.stackSize;
        }
        p_77654_3_.getFoodStats().func_151686_a(this, p_77654_1_);
        p_77654_2_.playSoundAtEntity(p_77654_3_, "random.burp", 0.5F, p_77654_2_.rand.nextFloat() * 0.1F + 0.9F);
        this.onFoodEaten(p_77654_1_, p_77654_2_, p_77654_3_);
        return p_77654_1_;
    }

    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
        p_77659_3_.setItemInUse(p_77659_1_, this.getMaxItemUseDuration(p_77659_1_));
        return p_77659_1_;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_)
    {
        p_77624_3_.add("Endurance (1:30)");
    }

}
