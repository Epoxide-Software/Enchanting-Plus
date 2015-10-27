package net.epoxide.eplus.gui;

import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiModEnchantmentTable extends GuiContainer {

    private final EntityPlayer player;
    private final ContainerEnchantTable container;
    private final int xPos;
    private final int yPos;
    private final int zPos;

    public GuiModEnchantmentTable (InventoryPlayer inventory, World world, int x, int y, int z, TileEntityEnchantingTable tileEntity) {

        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        this.player = inventory.player;

        this.container = (ContainerEnchantTable) inventorySlots;

        this.xPos = x;
        this.yPos = y;
        this.zPos = z;

        this.xSize = 235;
        this.ySize = 182;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float p_146976_1_, int p_146976_2_, int p_146976_3_) {

    }
}
