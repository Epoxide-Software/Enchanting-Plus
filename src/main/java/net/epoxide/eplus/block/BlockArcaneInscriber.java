package net.epoxide.eplus.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.darkhax.bookshelf.lib.util.EnchantmentUtils;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;

public class BlockArcaneInscriber extends BlockContainer {
    
    public BlockArcaneInscriber() {
        
        super(Material.wood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setHardness(1.5f);
        this.setBlockName("eplus.arcaneInscriber");
        this.setBlockTextureName("minecraft:bookshelf");
    }
    
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        
        player.triggerAchievement(ContentHandler.achievementStudies);
        ItemStack itemStack = player.getHeldItem();
        TileEntityArcaneInscriber tileEntity = (TileEntityArcaneInscriber) world.getTileEntity(x, y, z);
        
        if (itemStack != null) {
            if (itemStack.getItem() == Items.enchanted_book && tileEntity.getOutput() == null) {
                if (EnchantmentUtils.isStackEnchanted(itemStack)) {
                    
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
            else if (tileEntity.getEnchantmentBook() != null && tileEntity.getOutput() == null) {
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
            if (tileEntity.getEnchantmentBook() != null) {
                player.inventory.addItemStackToInventory(tileEntity.getEnchantmentBook());
                tileEntity.updateTileInfo();
                return true;
            }
        }
        else if (player.getHeldItem() == null) {
            if (tileEntity.getOutput() != null) {
                player.inventory.addItemStackToInventory(tileEntity.getOutput());
                tileEntity.updateTileInfo();
                tileEntity.setOutput(null);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        
        return new TileEntityArcaneInscriber();
    }
    
    @Override
    public void addCollisionBoxesToList (World world, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {
        
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        super.addCollisionBoxesToList(world, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        
        return side != 1 && side != 0 ? super.getIcon(side, meta) : Blocks.planks.getBlockTextureFromSide(side);
    }
}
