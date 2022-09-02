package net.tclproject.rpgstamina.fixes;

import cpw.mods.fml.common.Loader;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.FirstClassTransformer;

public class PatchesLoader extends CustomLoadingPlugin {
// Turns on MysteriumASM Lib. You can only have one patch loader at once.

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{FirstClassTransformer.class.getName()};
    }

    @Override
    public void registerFixes() {
        registerClassWithFixes("net.tclproject.rpgstamina.fixes.ModFixes");
    }
}
