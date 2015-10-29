package net.epoxide.eplus.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.epoxide.eplus.block.BlockEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ContentHandler {
    
    public static Block eplusTable;
    
    public static Item tableUpgrade;
    public static Item scroll;
    
    public static void initBlocks () {
        
        eplusTable = new BlockEnchantTable();
        GameRegistry.registerBlock(eplusTable, "advancedEnchantmentTable");
        GameRegistry.registerTileEntity(TileEntityEnchantingTable.class, "advancedEnchantmentTable");
    }
    
    public static void initItems () {
    
    }
}