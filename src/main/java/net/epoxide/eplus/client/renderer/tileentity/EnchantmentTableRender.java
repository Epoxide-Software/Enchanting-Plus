package net.epoxide.eplus.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import net.epoxide.eplus.lib.util.RenderUtil;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class EnchantmentTableRender extends TileEntitySpecialRenderer {
    
    private ResourceLocation texture = new ResourceLocation("eplus:textures/entity/enchantingplus_book.png");
    
    public void renderTileEntityEnchantmentTableAt (TileEntityEnchantTable tileEntity, double x, double y, double z, float tickPartial) {
        
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        RenderUtil.renderBook(texture, tileEntity.tickCount, tileEntity.pageFlipPrev, tileEntity.pageFlip, tileEntity.rotation, tileEntity.prevRotation, tileEntity.foldAmount, tileEntity.prevFoldAmount, tickPartial);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt (TileEntity tileEntity, double x, double y, double z, float tickPartial) {
        
        renderTileEntityEnchantmentTableAt((TileEntityEnchantTable) tileEntity, x, y, z, tickPartial);
    }
}
