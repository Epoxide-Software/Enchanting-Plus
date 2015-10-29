package net.epoxide.eplus.inventory;

import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class ContainerEnchantTable extends Container {
    
    public final World world;
    private final TileEntityEnchantTable tileEntityTable;
    private final int x;
    private final int y;
    private final int z;
    private final EntityPlayer player;
    
    public ContainerEnchantTable(final InventoryPlayer inventoryPlayer, World world, int x, int y, int z, TileEntityEnchantTable tileEntityTable) {
        
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.tileEntityTable = tileEntityTable;
        
        this.player = inventoryPlayer.player;
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer entityPlayer) {
        
        return entityPlayer.getDistanceSq((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D && !entityPlayer.isDead;
    }
}
