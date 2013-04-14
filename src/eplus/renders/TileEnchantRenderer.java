package eplus.renders;

import eplus.inventory.TileEnchantTable;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.entity.RenderEnchantmentTable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class TileEnchantRenderer extends TileEntitySpecialRenderer {
    private ModelBook enchantmentBook = new ModelBook();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
    {
        World world = tileentity.getWorldObj();
        int xCoord = tileentity.xCoord;
        int yCoord = tileentity.yCoord;
        int zCoord = tileentity.zCoord;

        if (((TileEnchantTable) tileentity).itemInTable != null && world.getBlockId(xCoord, yCoord + 1, zCoord) == 0) {
            this.renderTable((TileEnchantTable) tileentity, d0, d1, d2, f);
        } else {
            this.renderDefaultTable((TileEnchantTable) tileentity, d0, d1, d2, f);
        }
    }

    private void renderDefaultTable(TileEnchantTable tileentity, double d0, double d1, double d2, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d0 + 0.5F, (float)d1 + 0.75F, (float)d2 + 0.5F);
        float f1 = (float)tileentity.tickCount + f;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
        float f2;

        for (f2 = tileentity.bookRotation2 - tileentity.bookRotationPrev; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (f2 < -(float)Math.PI)
        {
            f2 += ((float)Math.PI * 2F);
        }

        float f3 = tileentity.bookRotationPrev + f2 * f;
        GL11.glRotatef(-f3 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTextureByName("/item/book.png");
        float f4 = tileentity.pageFlipPrev + (tileentity.pageFlip - tileentity.pageFlipPrev) * f + 0.25F;
        float f5 = tileentity.pageFlipPrev + (tileentity.pageFlip - tileentity.pageFlipPrev) * f + 0.75F;
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

        float f6 = tileentity.bookSpreadPrev + (tileentity.bookSpread - tileentity.bookSpreadPrev) * f;
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.enchantmentBook.render((Entity)null, f1, f4, f5, f6, 0.0F, 0.0625F);
        GL11.glPopMatrix();

    }

    private void renderTable(TileEnchantTable tileentity, double x, double y, double z, float f)
    {


        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        float f1 = (float) tileentity.tickCount + f;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);

        TableEntityItem entityItem = new TableEntityItem(tileentity.getWorldObj(), 0.0D, 0.0D, 0.0D, tileentity.itemInTable);
        entityItem.getEntityItem().stackSize = 1;
        entityItem.hoverStart = 0.0F;

        float f2;

        for (f2 = tileentity.bookRotation2 - tileentity.bookRotationPrev; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        float f3 = tileentity.bookRotationPrev + f2 * f;
        GL11.glPushMatrix();
        GL11.glRotatef(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90, 0.0f, 1.0f, 0.0f);

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }
}
