package net.epoxide.eplus.inventory;

import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class ContainerEnchantTable extends Container {
    
    public final World worldObj;
    private final TileEntityEnchantingTable tileEntityTable;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    private final EntityPlayer player;
    
    public ContainerEnchantTable(final InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, TileEntityEnchantingTable tileEntityTable) {
        
        this.worldObj = par2World;
        this.xPos = par3;
        this.yPos = par4;
        this.zPos = par5;
        
        this.tileEntityTable = tileEntityTable;
        
        this.player = par1InventoryPlayer.player;
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer entityPlayer) {
        
        return entityPlayer.getDistanceSq((double) this.xPos + 0.5D, (double) this.yPos + 0.5D, (double) this.zPos + 0.5D) <= 64.0D && !entityPlayer.isDead;
    }
}
