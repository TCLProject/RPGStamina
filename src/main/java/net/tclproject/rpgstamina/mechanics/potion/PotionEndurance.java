package net.tclproject.rpgstamina.mechanics.potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.tclproject.rpgstamina.api.ExtendedPlayer;

public class PotionEndurance extends Potion {

	public PotionEndurance(int id) {
		super(id, false, 0x56c93c);
		this.setPotionName("potion.staminaEndurance");
	}

	@SideOnly(Side.CLIENT)
	public int getStatusIconIndex() {
		return 2 + 2 * 8;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasStatusIcon() {
		return true;
	}

	public void performEffect(EntityLivingBase p_76394_1_, int p_76394_2_) {
	}

	public void applyAttributesModifiersToEntity(EntityLivingBase p_111185_1_, BaseAttributeMap p_111185_2_, int p_111185_3_) {
		if (p_111185_1_ instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) p_111185_1_;
			if (ExtendedPlayer.get(p) != null) ExtendedPlayer.get(p).gainStamina(250);
		}
		super.applyAttributesModifiersToEntity(p_111185_1_, p_111185_2_, p_111185_3_);
	}

}
