package eplus.common;

import net.minecraft.inventory.InventoryBasic;

public class InventoryEnchanting extends InventoryBasic
{
    final ContainerEnchanting container;

    public InventoryEnchanting(ContainerEnchanting containerEnchanting, String par2Str, int par3)
    {
        super(par2Str, par3);
        container = containerEnchanting;
    }

    public int getInventoryStackLimit()
    {
        return 1;
    }
}