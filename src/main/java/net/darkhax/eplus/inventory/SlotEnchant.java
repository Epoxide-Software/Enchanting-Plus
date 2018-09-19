package net.darkhax.eplus.inventory;

import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.EnchantmentLogicController;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotEnchant extends SlotItemHandler {

    private final EnchantmentLogicController logic;

    public SlotEnchant (EnchantmentLogicController logic, int index, int xPosition, int yPosition) {

        super(logic.getInventory(), index, xPosition, yPosition);
        this.logic = logic;
    }

    @Override
    public void onSlotChanged () {

        this.logic.onItemUpdated();
        super.onSlotChanged();
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