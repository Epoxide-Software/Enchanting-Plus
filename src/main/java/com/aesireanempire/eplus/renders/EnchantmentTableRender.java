package com.aesireanempire.eplus.renders;

import com.aesireanempire.eplus.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EnchantmentTableRender extends TileEntitySpecialRenderer
{
    private ModelBook enchantmentBook = new ModelBook();
    private ResourceLocation texture = new ResourceLocation(References.MODID, "textures/gui/enchantingplus_book.png");

    public void renderTileEntityEnchantmentTableAt(TileEntityEnchantmentTable table, double xPos, double yPos, double zPos, float tickPartial)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)xPos + 0.5F, (float)yPos + 0.75F, (float)zPos + 0.5F);
        float f1 = (float)table.field_145926_a + tickPartial;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
        float f2;

        for (f2 = table.field_145928_o - table.field_145925_p; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (f2 < -(float)Math.PI)
        {
            f2 += ((float)Math.PI * 2F);
        }

        float f3 = table.field_145925_p + f2 * tickPartial;
        GL11.glRotatef(-f3 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(texture);
        float f4 = table.field_145931_j + (table.field_145933_i - table.field_145931_j) * tickPartial + 0.25F;
        float f5 = table.field_145931_j + (table.field_145933_i - table.field_145931_j) * tickPartial + 0.75F;
        f4 = (f4 - (float)MathHelper.truncateDoubleToInt((double)f4)) * 1.6F - 0.3F;
        f5 = (f5 - (float)MathHelper.truncateDoubleToInt((double)f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F)
        {
            f4 = 0.0F;
        }

        if (f5 < 0.0F)
        {
            f5 = 0.0F;
        }

        if (f4 > 1.0F)
        {
            f4 = 1.0F;
        }

        if (f5 > 1.0F)
        {
            f5 = 1.0F;
        }

        float f6 = table.field_145927_n + (table.field_145930_m - table.field_145927_n) * tickPartial;
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.enchantmentBook.render((Entity)null, f1, f4, f5, f6, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntityEnchantmentTableAt((TileEntityEnchantmentTable) par1TileEntity, par2, par4, par6, par8);
    }
}
