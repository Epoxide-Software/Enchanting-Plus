package net.epoxide.eplus.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;

public class ItemTableUpgrade extends Item {
    
    public ItemTableUpgrade() {
        
        this.setMaxStackSize(16);
        this.setUnlocalizedName("tableUpgrade");
        this.setCreativeTab(EnchantingPlus.tabEplus);
    }
    
    @Override
    public void registerIcons (IIconRegister iconRegister) {
        
        this.itemIcon = iconRegister.registerIcon("eplus:enchanting_table_upgrade");
    }
    
    @Override
    public boolean onItemUse (ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        
        if (!world.isRemote) {
            Block block = world.getBlock(x, y, z);
            if (block instanceof BlockEnchantmentTable) {
                world.setBlock(x, y, z, ContentHandler.blockAdvancedTable);
                world.setTileEntity(x, y, z, new TileEntityEnchantTable());
                itemStack.stackSize--;
                return true;
            }
        }
        return false;
    }
}
