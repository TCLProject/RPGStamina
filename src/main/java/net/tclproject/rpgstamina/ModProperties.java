package net.tclproject.rpgstamina;

import java.io.File;
import net.minecraft.util.EnumChatFormatting;



public class ModProperties {
// This class will be used to define mod properties in order to access them from anywhere.

	// General values
	public static final String MODID = "rpgstamina";
	public static final String NAME = "RPGStamina";
	public static final String VERSION = "2.0";
	public static final String MC_VERSION = "1.7.10";
	public static final String URL = "";
	public static final String VERSION_CHECKER_URL = "";
	public static final String GUI_FACTORY_CLASS = "net.tclproject.rpgstamina.config.gui.GuiFactory";


	// Mod info page
	public static final String COLORED_NAME = EnumChatFormatting.GRAY + "RPG" + EnumChatFormatting.DARK_GREEN + "Stamina";

	public static final String COLORED_VERSION = EnumChatFormatting.GRAY + VERSION;
	public static final String COLORED_URL = EnumChatFormatting.GRAY + URL;

	public static final String CREDITS = "";

	public static final String[] AUTHORS = new String[] {
			EnumChatFormatting.RED + "HRudyPlayZ",
			EnumChatFormatting.DARK_PURPLE + "Matrix (TCLProject)",
	};

	public static final String DESCRIPTION = EnumChatFormatting.GRAY + "A mod that adds a highly configurable stamina bar to the game. \nLearn more on the mod's page.";

	public static final String[] SPLASH_OF_THE_DAY = new String[] {
			"Darling, this is good!",
			"The most complete stamina mod of any version!",
			"Finally, a customisable stamina mod!",
			"Only made possible by Notch's most realistic LEGO Simulator built so far.",
			"I love it.",
			"Can i still say i'm learning Java so people don't hate me for bugs or that's too late now?",
			"Mitochondria is the powerhouse of the cell.",
			"And it's not made in MCreator!",
			"Creeper? Awww man.",
			"Also try Immersive Cavegen.",
			"Also try Battlegear.",
			"Also try SmartMoving.",
			"Also try ModernWarfare.",
			"Also try The Offhand Mod.",
			"Also try MCInstance Loader.",
			"Also try our other mods.",
			"Finally released!",
			"2.0.0.0.0.0, also known as 2.0!",
			"RPGStamina 2.0: Electric boogaloo, but actually The Overhaul!",
			"RPGStamina 2: Catching Fire",
			"With a fresh coat of paint!",
			"Entirely redone!",
			"Better than ever!",
			"The next version is looking, pretty, good!",
			"Since you're here, you might want to support the mod on Github and Curseforge!",
			"The most config you can get, in one package.",
			"The revolution in modding history.",
			"Jeff Bezos's well kept secret.",
			"Why were the dwarves digging a hole? To get to this sooner!",
			"Elon Musk's hidden fetish.",
			"if thisModWorks() then thatsAwesome() else pleaseReportIssue() end",
			"Why did the chicken cross the road? Because this mod was waiting on the other side.",
			"I would like to first of all thank my two parents, without whom i wouldn't be here.",
			"And i think to myself, what a wonderful world.",
			"It's like crypto but actually stable!"
	};

	// Should be equal to null to disable it, otherwise it should just be the file name (ex: "logo.png").
	public static String LOGO = "assets/" + ModProperties.MODID + "/logo.png";
}
