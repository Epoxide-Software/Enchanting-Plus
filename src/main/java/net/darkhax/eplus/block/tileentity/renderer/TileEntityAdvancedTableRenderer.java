package net.darkhax.eplus.block.tileentity.renderer;

import net.darkhax.bookshelf.util.ParticleUtils;
import net.darkhax.eplus.EnchLogic;
import net.darkhax.eplus.block.tileentity.TileEntityWithBook;
import net.minecraft.util.EnumParticleTypes;
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

    @Override
    public void render (TileEntityWithBook te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        if (EnchLogic.isWikedNight(te.getWorld()) && te.bookSpread != 0) {

            ParticleUtils.spawnParticleRing(te.getWorld(), EnumParticleTypes.PORTAL, te.getPos().getX() + 0.5, te.getPos().getY() + 1, te.getPos().getZ() + 0.5, 0, -1, 0, 0.45);
        }

        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
