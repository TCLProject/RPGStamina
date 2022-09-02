package net.tclproject.rpgstamina.mechanics.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentLightFeet extends Enchantment {

    public EnchantmentLightFeet(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.armor_feet);
        this.setName("lightBoots");
    }

    public int getMinEnchantability(int p_77321_1_)
    {
        return 10 + (p_77321_1_ * 10);
    }

    public int getMaxEnchantability(int p_77321_1_) {
        return this.getMinEnchantability(p_77321_1_) + 10;
    }

    public int getMaxLevel() {
        return 2;
    }
}
