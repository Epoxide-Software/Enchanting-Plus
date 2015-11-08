package net.epoxide.eplus.client.renderer.tileentity;

import net.epoxide.eplus.tileentity.TileEntityArcaneDisenchanter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ArcaneDisenchanterRender extends TileEntitySpecialRenderer {
    public static boolean graphicsCache;
    private ModelBook enchantmentBook = new ModelBook();
    private ResourceLocation texture = new ResourceLocation("eplus:textures/entity/enchantingplus_book.png");

    private void renderTileEntityArcaneDisenchaterAt (TileEntityArcaneDisenchanter tileEntity, double x, double y, double z, float tickPartial) {

        GL11.glPushMatrix();
        GL11.glTranslated(x - 0.29, y, z - 0.29);
        if (tileEntity.hasModifiers(0)) {
            renderItemStack(tileEntity, tileEntity.getModifier(0).stack);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.29, y, z + 0.29);
        if (tileEntity.hasModifiers(1)) {
            renderItemStack(tileEntity, tileEntity.getModifier(1).stack);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if (tileEntity.hasEnchantmentBook()) {
            renderBook(tileEntity, tickPartial);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if (tileEntity.getOutput() != null) {
            renderItemStack(tileEntity, tileEntity.getOutput());
        }
        GL11.glPopMatrix();
    }

    private void renderBook (TileEntityArcaneDisenchanter tileEntity, float tickPartial) {

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.75F, 0.5F);
        float f1 = (float) tileEntity.tickCount + tickPartial;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
        float f2;

        f2 = tileEntity.field_145928_o - tileEntity.field_145925_p;
        while (f2 >= (float) Math.PI) {
            f2 -= ((float) Math.PI * 2F);
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        float f3 = tileEntity.field_145925_p + f2 * tickPartial;
        GL11.glRotatef(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(texture);
        float f4 = tileEntity.pageFlipPrev + (tileEntity.pageFlip - tileEntity.pageFlipPrev) * tickPartial + 0.25F;
        float f5 = tileEntity.pageFlipPrev + (tileEntity.pageFlip - tileEntity.pageFlipPrev) * tickPartial + 0.75F;
        f4 = (f4 - (float) MathHelper.truncateDoubleToInt((double) f4)) * 1.6F - 0.3F;
        f5 = (f5 - (float) MathHelper.truncateDoubleToInt((double) f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        if (f5 < 0.0F) {
            f5 = 0.0F;
        }

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }

        float f6 = tileEntity.field_145927_n + (tileEntity.field_145930_m - tileEntity.field_145927_n) * tickPartial;
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.enchantmentBook.render(null, f1, f4, f5, f6, 0.0F, 0.0625F);
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

        renderTileEntityArcaneDisenchaterAt((TileEntityArcaneDisenchanter) tileEntity, x, y, z, tickPartial);
    }

}
