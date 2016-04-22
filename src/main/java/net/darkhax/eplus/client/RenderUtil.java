package net.darkhax.eplus.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
    
    private static ModelBook enchantmentBook = new ModelBook();
    
    public static void renderBook (ResourceLocation location, float tickCount, float pageFlipPrev, float pageFlip, float rotation, float prevRotation, float foldAmount, float prevFoldAmount, float tickPartial) {
        
        GL11.glPushMatrix();
        final float f1 = tickCount + tickPartial;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
        float f2;
        
        f2 = rotation - prevRotation;
        while (f2 >= (float) Math.PI)
            f2 -= (float) Math.PI * 2F;
            
        while (f2 < -(float) Math.PI)
            f2 += (float) Math.PI * 2F;
            
        final float f3 = prevRotation + f2 * tickPartial;
        GL11.glRotatef(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(location);
        float f4 = pageFlipPrev + (pageFlip - pageFlipPrev) * tickPartial + 0.25F;
        float f5 = pageFlipPrev + (pageFlip - pageFlipPrev) * tickPartial + 0.75F;
        f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
        f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
        
        if (f4 < 0.0F)
            f4 = 0.0F;
            
        if (f5 < 0.0F)
            f5 = 0.0F;
            
        if (f4 > 1.0F)
            f4 = 1.0F;
            
        if (f5 > 1.0F)
            f5 = 1.0F;
            
        final float f6 = prevFoldAmount + (foldAmount - prevFoldAmount) * tickPartial;
        GL11.glEnable(GL11.GL_CULL_FACE);
        enchantmentBook.render(null, f1, f4, f5, f6, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}
