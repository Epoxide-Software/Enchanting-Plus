package eplus.common;

import net.minecraft.inventory.InventoryBasic;

@SuppressWarnings("ALL")
public class InventoryEnchanting extends InventoryBasic
{
    final ContainerEnchanting container;

    public InventoryEnchanting(ContainerEnchanting containerEnchanting, String par2Str, int par3)
    {
        super(par2Str, false, par3);
        container = containerEnchanting;
    }

    public int getInventoryStackLimit()
    {
        return 1;
    }
}