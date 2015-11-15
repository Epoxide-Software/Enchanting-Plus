package net.epoxide.eplus.client.renderer.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.IItemRenderer;

import net.epoxide.eplus.lib.util.RenderUtil;

public class RenderItemEnchantedTome implements IItemRenderer {
    
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
        GL11.glTranslatef(0.25f, 0.19f, 0f);
        GL11.glRotatef(90f, 1, 0, 1);
        GL11.glRotatef(45f, 0, -1, 0);
        
        if (type == ItemRenderType.ENTITY) {
            
            GL11.glRotatef(55f, 0, 1, 0);
            GL11.glScalef(1f, 1f, 1f);
        }
        
        else
            GL11.glScalef(2f, 2f, 2f);
            
        if (type == ItemRenderType.EQUIPPED)
            GL11.glTranslatef(0.15f, 0f, -0.35f);
            
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            
            GL11.glTranslatef(0.8f, -0.3f, 0f);
            GL11.glRotatef(145f, 0f, -0f, 1f);
        }
        
        RenderUtil.renderBook(texture, 0f, 1f, 1f, 1f, 1f, 0.01f, 0.01f, 0f);
        GL11.glPopMatrix();
    }
}