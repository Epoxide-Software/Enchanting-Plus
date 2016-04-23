package net.darkhax.eplus.item;

import net.darkhax.eplus.EnchantingPlus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTableUpgrade extends Item {
    
    public ItemTableUpgrade() {
        
        this.setMaxStackSize(16);
        this.setUnlocalizedName("eplus.tableUpgrade");
        this.setCreativeTab(EnchantingPlus.tabEplus);
    }
    
    @Override
    public EnumActionResult onItemUse (ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        if (!worldIn.isRemote)
            // TODO Add code to upgrade a vanilla table.
            return EnumActionResult.SUCCESS;
            
        return EnumActionResult.PASS;
    }
}