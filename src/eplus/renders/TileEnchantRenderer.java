package eplus.renders;

import eplus.inventory.TileEnchantTable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class TileEnchantRenderer extends TileEntitySpecialRenderer {


    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
    {
        World world = tileentity.getWorldObj();
        int xCoord = tileentity.xCoord;
        int yCoord = tileentity.yCoord;
        int zCoord = tileentity.zCoord;

        if (((TileEnchantTable) tileentity).itemInTable != null && world.getBlockId(xCoord, yCoord + 1, zCoord) == 0) {
            this.renderTable((TileEnchantTable) tileentity, d0, d1, d2, f);
        }
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
