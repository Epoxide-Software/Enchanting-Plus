package net.epoxide.eplus.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.inventory.EnchantHelper;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.epoxide.eplus.tileentity.TileEntityArcaneDisenchanter;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockArcaneDisenchanter extends BlockContainer {
    
    public BlockArcaneDisenchanter() {
        
        super(Material.iron);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setBlockName("arcaneDisenchanter");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isOpaqueCube () {
        
        return false;
    }
    
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        
        ItemStack itemStack = player.getHeldItem();
        TileEntityArcaneDisenchanter tileEntity = (TileEntityArcaneDisenchanter) world.getTileEntity(x, y, z);
        
        if (itemStack != null) {
            if (itemStack.getItem() == Items.enchanted_book && tileEntity.getOutput() == null) {
                if (EnchantHelper.isItemEnchanted(itemStack)) {
                    
                    if (tileEntity.getEnchantmentBook() == null) {
                        tileEntity.setEnchantmentBook(itemStack);
                        itemStack.stackSize--;
                        return false;
                    }
                    else {
                        return true;
                    }
                }
            }
            else if (tileEntity.getEnchantmentBook()!=null && tileEntity.getOutput() == null) {
                for (ScrollModifier modifier : ContentHandler.modifiers) {
                    if (modifier.stack.getItem() == itemStack.getItem()) {
                        if (tileEntity.addModifiers(modifier)) {
                            itemStack.stackSize--;
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        else if (player.isSneaking()) {
            if (tileEntity.getEnchantmentBook()!=null) {
                player.inventory.addItemStackToInventory(tileEntity.getEnchantmentBook());
                tileEntity.setEnchantmentBook(null);
                tileEntity.clearModifiers();
                return true;
            }
        }
        else if (player.getHeldItem() == null) {
            if (tileEntity.getOutput()!=null) {
                player.inventory.addItemStackToInventory(tileEntity.getOutput());
                tileEntity.setOutput(null);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        
        return new TileEntityArcaneDisenchanter();
    }
    
    @Override
    public void addCollisionBoxesToList (World world, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {
        
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        super.addCollisionBoxesToList(world, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
    }
}
