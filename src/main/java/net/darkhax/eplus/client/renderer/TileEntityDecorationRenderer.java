package net.darkhax.eplus.client.renderer;

import java.awt.Color;

import net.darkhax.eplus.tileentity.TileEntityDecoration;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TileEntityDecorationRenderer extends TileEntitySpecialRenderer<TileEntityDecoration> {
    
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] { new ResourceLocation("eplus", "textures/entity/enchantingplus_book.png"), new ResourceLocation("minecraft", "textures/entity/enchanting_table_book.png"), new ResourceLocation("eplus", "textures/entity/prismarine_book.png"), new ResourceLocation("eplus", "textures/entity/nether_book.png"), new ResourceLocation("eplus", "textures/entity/tartarite_book.png"), new ResourceLocation("eplus", "textures/entity/white_book.png"), new ResourceLocation("eplus", "textures/entity/metal_book.png") };
    
    private final ModelBook modelBook = new ModelBook();
    
    private ResourceLocation getTexture (int meta) {
        
        return meta >= 0 && meta < TEXTURES.length ? TEXTURES[meta] : TEXTURES[0];
    }
    
    @Override
    public void renderTileEntityAt (TileEntityDecoration te, double x, double y, double z, float partialTicks, int destroyStage) {
        
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F + te.height, (float) z + 0.5F);
        final float f = te.tickCount + partialTicks;
        GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
        
        final Color color = new Color(te.color);
        GlStateManager.color((float) color.getRed() / (float) 255, (float) color.getGreen() / (float) 255, (float) color.getBlue() / (float) 255);
        float f1;
        
        for (f1 = te.bookRotation - te.bookRotationPrev; f1 >= (float) Math.PI; f1 -= (float) Math.PI * 2F)
            ;
        
        while (f1 < -(float) Math.PI)
            f1 += (float) Math.PI * 2F;
        
        final float f2 = te.bookRotationPrev + f1 * partialTicks;
        GlStateManager.rotate(-f2 * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(this.getTexture(te.variant));
        float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
        float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
        f3 = (f3 - MathHelper.truncateDoubleToInt(f3)) * 1.6F - 0.3F;
        f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
        
        if (f3 < 0.0F)
            f3 = 0.0F;
        
        if (f4 < 0.0F)
            f4 = 0.0F;
        
        if (f3 > 1.0F)
            f3 = 1.0F;
        
        if (f4 > 1.0F)
            f4 = 1.0F;
        
        final float f5 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
        GlStateManager.enableCull();
        this.modelBook.render((Entity) null, f, f3, f4, f5, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
