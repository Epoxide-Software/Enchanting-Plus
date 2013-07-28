package eplus.items;

import eplus.inventory.TileEnchantTable;
import eplus.lib.ConfigurationSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTableUpgrade extends Item
{

    public ItemTableUpgrade(int id)
    {
        super(id);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("eplus:enchanting_table_upgrade");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        if (!world.isRemote)
        {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];

            if (block instanceof BlockEnchantmentTable)
            {
                world.setBlock(x, y, z, ConfigurationSettings.tableID);
                world.setBlockTileEntity(x, y, z, new TileEnchantTable());
                itemStack.stackSize--;
                return true;
            }
        }

        return false;
    }

}
