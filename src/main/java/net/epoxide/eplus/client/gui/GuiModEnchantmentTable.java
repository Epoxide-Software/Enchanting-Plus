package net.epoxide.eplus.client.gui;

import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiModEnchantmentTable extends GuiContainer {
    
    private final EntityPlayer player;
    private final ContainerEnchantTable container;
    private final int x;
    private final int y;
    private final int z;
    
    public GuiModEnchantmentTable(InventoryPlayer inventory, World world, int x, int y, int z, TileEntityEnchantTable tileEntity) {
        
        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        this.player = inventory.player;
        
        this.container = (ContainerEnchantTable) inventorySlots;
        
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.xSize = 235;
        this.ySize = 182;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {

    }
}
