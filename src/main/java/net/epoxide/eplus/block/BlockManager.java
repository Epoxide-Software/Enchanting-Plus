package net.epoxide.eplus.block;

import net.minecraft.block.Block;

public class BlockManager {

    public static Block enchantingTable;

    public BlockManager(){

        enchantingTable = new BlockEnchantTable();
    }
}
