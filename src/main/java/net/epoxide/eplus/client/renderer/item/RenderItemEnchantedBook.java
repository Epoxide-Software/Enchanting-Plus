package net.epoxide.eplus.client.renderer.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.IItemRenderer;

import net.epoxide.eplus.lib.util.RenderUtil;

public class RenderItemEnchantedBook implements IItemRenderer {
    
    private static ResourceLocation texture = new ResourceLocation("eplus:textures/entity/enchantingplus_book.png");
    
    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type) {
        
        return true;
    }
    
    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        
        return true;
    }
    
    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data) {
        
        GL11.glPushMatrix();
        GL11.glScalef(2f, 2, 2f);
        
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            
            GL11.glTranslatef(0.8f, 0.15f, 0f);
            GL11.glRotatef(90f, -1f, 1f, 1f);
        }
        
        RenderUtil.renderBook(texture, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 0f);
        GL11.glPopMatrix();
    }
}