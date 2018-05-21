package net.darkhax.eplus.inventory;

import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotEnchant extends SlotItemHandler {

    public SlotEnchant (TileEntityAdvancedTable tile, int index, int xPosition, int yPosition) {

        super(tile.getInventory(), index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit () {

        return 1;
    }

    @Override
    public boolean isItemValid (ItemStack stack) {

        return EnchantingPlus.TEST_ENCHANTABILITY.test(stack);
    }
}