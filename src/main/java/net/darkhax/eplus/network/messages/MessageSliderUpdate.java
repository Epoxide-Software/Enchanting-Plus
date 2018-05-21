package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.bookshelf.network.TileEntityMessage;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MessageSliderUpdate extends TileEntityMessage {

    public EnchantData updatedEnchant;

    public MessageSliderUpdate () {

    }

    public MessageSliderUpdate (BlockPos tablePos, EnchantData updatedEnchant) {

        super(tablePos);
        this.updatedEnchant = updatedEnchant;
    }

    @Override
    public void getAction () {

        final World world = context.getServerHandler().player.world;
        ((TileEntityAdvancedTable) world.getTileEntity(this.pos)).getLogic().updateEnchantment(this.updatedEnchant.enchantment, this.updatedEnchant.enchantmentLevel);
    }
}
