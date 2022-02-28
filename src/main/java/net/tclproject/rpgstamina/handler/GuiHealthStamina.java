package net.tclproject.rpgstamina.handler;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.tclproject.rpgstamina.PatchesConfig;

public class GuiHealthStamina extends Gui {
	
	   private Minecraft mc;
	   private static final ResourceLocation texturepath = new ResourceLocation("rpgstamina", "textures/stamina.png");
	   private static final ResourceLocation texturepath2 = new ResourceLocation("rpgstamina", "textures/staminaempty.png");
	   public static RenderItem itemRender = new RenderItem();
	   
	   public GuiHealthStamina(Minecraft mc)
		{
			super();
			// We need this to invoke the render engine.
			this.mc = mc;
		}

	   @SubscribeEvent(
	      priority = EventPriority.NORMAL
	   )
	   public void renderHealthStaminaOverlays(RenderGameOverlayEvent event) {
	    	if (PatchesConfig.enableReplaceFood && GuiIngameForge.renderFood) {
	    		GuiIngameForge.renderFood = false;
	    	}
	    	
		   if (event.isCancelable() || event.type != ElementType.HOTBAR || this.mc.thePlayer.capabilities.isCreativeMode)
			{
				return;
			}

			ExtendedPlayer props = ExtendedPlayer.get(this.mc.thePlayer);
			
			if (props == null || props.maxStamina == 0)
			{
				return;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			// Somewhere in Minecraft vanilla code it says to do this because of a lighting bug
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_ALPHA_TEST);

		   if (PatchesConfig.staminaEmptyBar) {
			   Minecraft.getMinecraft().getTextureManager().bindTexture(texturepath2);
			   this.drawTexturedModalRect(event.resolution.getScaledWidth() / 2 + 10 + PatchesConfig.staminaXOffset, event.resolution.getScaledHeight() - (PatchesConfig.enableReplaceFood ? 39 : 50) + PatchesConfig.staminaYOffset - (PatchesConfig.staminaYOffset > -8 && mc.thePlayer.worldObj.getBlock((int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ).getMaterial() == Material.water ? 9 : 0), 0, 0, 81, 9);
		   }

			Minecraft.getMinecraft().getTextureManager().bindTexture(texturepath);

			int staminabarwidth = (int)(((float) props.currentStamina / props.getMaxStamina()) * 81);

			this.drawTexturedModalRect(event.resolution.getScaledWidth() / 2 + 10 + PatchesConfig.staminaXOffset, event.resolution.getScaledHeight() - (PatchesConfig.enableReplaceFood ? 39 : 50) + PatchesConfig.staminaYOffset - (PatchesConfig.staminaYOffset > -8 && mc.thePlayer.worldObj.getBlock((int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ).getMaterial() == Material.water ? 9 : 0), 0, 0, staminabarwidth, 9);

		   GL11.glDisable(GL11.GL_BLEND);
		   GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glDepthMask(true);
			GL11.glPopMatrix();
	   }
}
