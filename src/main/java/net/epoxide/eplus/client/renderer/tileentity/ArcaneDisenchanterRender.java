package net.epoxide.eplus.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import net.epoxide.eplus.lib.util.RenderUtil;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ArcaneDisenchanterRender extends TileEntitySpecialRenderer {
    public static boolean graphicsCache;
    private ResourceLocation texture = new ResourceLocation("eplus:textures/entity/enchantingplus_book.png");
    
    private void renderTileEntityArcaneDisenchaterAt (TileEntityArcaneInscriber tileEntity, double x, double y, double z, float tickPartial) {
        
        GL11.glPushMatrix();
        GL11.glTranslated(x - 0.29, y, z - 0.29);
        if (tileEntity.getFirstModifier() != null) {
            renderItemStack(tileEntity, tileEntity.getFirstModifier().stack);
        }
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.29, y, z + 0.29);
        if (tileEntity.getSecondModifier() != null) {
            renderItemStack(tileEntity, tileEntity.getSecondModifier().stack);
        }
        GL11.glPopMatrix();
        
        if (tileEntity.getEnchantmentBook() != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            RenderUtil.renderBook(texture, tileEntity.tickCount, tileEntity.pageFlipPrev, tileEntity.pageFlip, tileEntity.rotation, tileEntity.prevRotation, tileEntity.foldAmount, tileEntity.prevFoldAmount, tickPartial);
            GL11.glPopMatrix();
        }
        
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if (tileEntity.getOutput() != null) {
            renderItemStack(tileEntity, tileEntity.getOutput());
        }
        GL11.glPopMatrix();
    }
    
    private void renderItemStack (TileEntity tileEntity, ItemStack itemStack) {
        
        GL11.glTranslated(0.5, 0, 0.5);
        if (itemStack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
            GL11.glTranslated(0, 0.55, 0);
        }
        else {
            GL11.glTranslated(0, 0.5, -(0.0625F * 3.65));
            GL11.glRotated(90, 1, 0, 0);
        }
        
        forceGraphics(true);
        
        EntityItem item = new EntityItem(tileEntity.getWorldObj(), 0, 0, 0, itemStack);
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0;
        
        RenderManager.instance.renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);
        
        resetGraphics();
    }
    
    public static void forceGraphics (boolean fancy) {
        
        graphicsCache = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        Minecraft.getMinecraft().gameSettings.fancyGraphics = fancy;
    }
    
    public static void resetGraphics () {
        
        Minecraft.getMinecraft().gameSettings.fancyGraphics = graphicsCache;
    }
    
    @Override
    public void renderTileEntityAt (TileEntity tileEntity, double x, double y, double z, float tickPartial) {
        
        renderTileEntityArcaneDisenchaterAt((TileEntityArcaneInscriber) tileEntity, x, y, z, tickPartial);
    }
    
}
