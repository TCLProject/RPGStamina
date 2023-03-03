package net.tclproject.rpgstamina.config.gui;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import java.util.ArrayList;
import net.tclproject.rpgstamina.ModProperties;
import net.tclproject.rpgstamina.config.Config;

@SideOnly(Side.CLIENT)
public class GuiConfig extends cpw.mods.fml.client.config.GuiConfig {

	public static ArrayList<IConfigElement> array = new ArrayList<>();

	public GuiConfig(GuiScreen parent) {
		super(parent,
		      array,
		      ModProperties.MODID,
		      false,
		      false,
		      Config.config.toString());
	}
}
