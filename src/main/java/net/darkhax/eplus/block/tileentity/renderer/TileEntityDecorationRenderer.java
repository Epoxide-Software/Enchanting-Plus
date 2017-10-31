package net.darkhax.eplus.block.tileentity.renderer;

import java.awt.Color;

import net.darkhax.eplus.block.tileentity.TileEntityDecoration;
import net.darkhax.eplus.block.tileentity.TileEntityWithBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TileEntityDecorationRenderer extends TileEntityBookRenderer {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] { new ResourceLocation("eplus", "textures/entity/enchantingplus_book.png"), new ResourceLocation("minecraft", "textures/entity/enchanting_table_book.png"), new ResourceLocation("eplus", "textures/entity/prismarine_book.png"), new ResourceLocation("eplus", "textures/entity/nether_book.png"), new ResourceLocation("eplus", "textures/entity/tartarite_book.png"), new ResourceLocation("eplus", "textures/entity/white_book.png"), new ResourceLocation("eplus", "textures/entity/metal_book.png") };

    @Override
    ResourceLocation getTexture (TileEntityWithBook tile) {

        final int meta = tile instanceof TileEntityDecoration ? ((TileEntityDecoration) tile).variant : tile.getBlockMetadata();
        return meta >= 0 && meta < TEXTURES.length ? TEXTURES[meta] : TEXTURES[0];
    }

    @Override
    float getHeightOffset (TileEntityWithBook tile) {

        return tile instanceof TileEntityDecoration ? 0.75f + ((TileEntityDecoration) tile).height : 0.75f;
    }

    @Override
    public void applyRenderEffects (TileEntityWithBook tile) {

        if (tile instanceof TileEntityDecoration) {

            final Color color = new Color(((TileEntityDecoration) tile).color);
            GlStateManager.color((float) color.getRed() / (float) 255, (float) color.getGreen() / (float) 255, (float) color.getBlue() / (float) 255);
        }
    }
}
