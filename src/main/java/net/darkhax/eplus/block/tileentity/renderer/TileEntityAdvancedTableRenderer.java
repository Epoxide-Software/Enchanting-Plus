package net.darkhax.eplus.block.tileentity.renderer;

import net.darkhax.eplus.block.tileentity.TileEntityWithBook;
import net.minecraft.util.ResourceLocation;

public class TileEntityAdvancedTableRenderer extends TileEntityBookRenderer {

    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("eplus:textures/entity/enchantingplus_book.png");

    @Override
    ResourceLocation getTexture (TileEntityWithBook tile) {

        return TEXTURE_BOOK;
    }

    @Override
    float getHeightOffset (TileEntityWithBook tile) {

        return 0.75f;
    }
}
