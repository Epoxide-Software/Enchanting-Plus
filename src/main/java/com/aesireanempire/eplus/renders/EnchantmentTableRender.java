package com.aesireanempire.eplus.renders;

import com.aesireanempire.eplus.lib.References;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class EnchantmentTableRender extends TileEntitySpecialRenderer
{
    private ModelBook enchantmentBook = new ModelBook();
    private ResourceLocation texture = new ResourceLocation(References.MODID, "/assets/eplus/textures/gui/enchantingplus_book.png");

    public void renderTileEntityEnchantmentTableAt(TileEntityEnchantmentTable par1TileEntityEnchantmentTable, double par2, double par4, double par6, float par8)
    {

    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        //this.renderTileEntityEnchantmentTableAt((TileEntityEnchantmentTable) par1TileEntity, par2, par4, par6, par8);
    }
}
