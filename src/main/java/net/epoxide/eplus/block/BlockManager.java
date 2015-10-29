package net.epoxide.eplus.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.block.Block;

public class BlockManager {
    
    public static Block enchantingTable;
    
    public BlockManager() {
        
        enchantingTable = new BlockEnchantTable();
        GameRegistry.registerBlock(enchantingTable, enchantingTable.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityEnchantingTable.class, enchantingTable.getUnlocalizedName());
    }
    
}
