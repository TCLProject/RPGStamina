package net.tclproject.rpgstamina.config;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import java.util.ArrayList;


public class PlayerStage {
	// Punishment stages
	public String stageMaximumValue = "disabled";
	public int realValue = -1;

	public boolean preventFoodEating = false;
	public boolean preventJump = false;
	public boolean preventSprint = false;
	public boolean preventAttack = false;

	public boolean useWhitelistForItemUseList = false;

	public String[] itemUseStrings = new String[0];
	public ArrayList<Item> internalItemUseList = new ArrayList<>();
	public String[] potionEffects = new String[0];

	public PlayerStage(String stageMaximumValue, boolean preventFoodEating, boolean preventJump, boolean preventSprint, boolean preventAttack, boolean useWhitelistForItemUseList, String[] itemUseStrings, String[] potionEffects) {
		this.stageMaximumValue = stageMaximumValue;
		this.preventFoodEating = preventFoodEating;
		this.preventJump = preventJump;
		this.preventSprint = preventSprint;
		this.preventAttack = preventAttack;
		this.useWhitelistForItemUseList = useWhitelistForItemUseList;

		this.itemUseStrings = itemUseStrings;
		for (String s : this.itemUseStrings) {
			String[] splittedItem = s.split(":");
			if (splittedItem.length >= 2) this.internalItemUseList.add(GameRegistry.findItem(splittedItem[0], splittedItem[1]));
		}

		this.potionEffects = potionEffects;
	}

	public PlayerStage() {}
}
