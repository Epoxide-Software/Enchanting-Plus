package net.darkhax.eplus.inventory;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.inventory.SlotArmor;
import net.darkhax.bookshelf.util.EntityUtils;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerAdvancedTable extends Container {

    public final World world;
    public final BlockPos pos;
    public final EntityPlayer player;
    public final TileEntityAdvancedTable table;

    public ContainerAdvancedTable (InventoryPlayer invPlayer, TileEntityAdvancedTable table) {

        this.player = invPlayer.player;
        this.table = table;
        this.world = table.getWorld();
        this.pos = table.getPos();

        this.addSlotToContainer(new SlotEnchant(table, 0, 37, 17));

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(invPlayer, x, 43 + 18 * x, 149));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 43 + 18 * x, 91 + y * 18));
            }
        }
        for (int y = 0; y < 4; y++) {
            this.addSlotToContainer(new SlotArmor(invPlayer.player, EntityUtils.getEquipmentSlot(y), invPlayer, 39 - y, 7, 24 + y * 19));
        }
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer entityPlayer, int idx) {

        ItemStack itemStack;
        final Slot clickSlot = this.inventorySlots.get(idx);

        if (clickSlot != null && clickSlot.getHasStack()) {
            itemStack = clickSlot.getStack();

            if (itemStack == ItemStack.EMPTY) {
                return ItemStack.EMPTY;
            }
            final List<Slot> selectedSlots = new ArrayList<>();

            if (clickSlot.inventory instanceof InventoryPlayer) {
                for (int x = 0; x < this.inventorySlots.size(); x++) {
                    final Slot advSlot = this.inventorySlots.get(x);
                    if (advSlot.isItemValid(itemStack)) {
                        selectedSlots.add(advSlot);
                    }
                }
            }
            else {
                for (int x = 0; x < this.inventorySlots.size(); x++) {
                    final Slot advSlot = this.inventorySlots.get(x);

                    if (advSlot.inventory instanceof InventoryPlayer) {
                        if (advSlot.isItemValid(itemStack)) {
                            selectedSlots.add(advSlot);
                        }
                    }
                }
            }

            if (itemStack != ItemStack.EMPTY) {
                for (final Slot slot : selectedSlots) {
                    if (slot.isItemValid(itemStack) && itemStack != ItemStack.EMPTY) {
                        if (slot.getHasStack()) {
                            final ItemStack stack = slot.getStack();

                            if (!itemStack.isEmpty() && itemStack.isItemEqual(stack)) {
                                int maxSize = stack.getMaxStackSize();

                                if (maxSize > slot.getSlotStackLimit()) {
                                    maxSize = slot.getSlotStackLimit();
                                }

                                int placeAble = maxSize - stack.getCount();

                                if (itemStack.getCount() < placeAble) {
                                    placeAble = itemStack.getCount();
                                }

                                stack.grow(placeAble);
                                itemStack.shrink(placeAble);

                                if (itemStack.getCount() <= 0) {
                                    clickSlot.putStack(ItemStack.EMPTY);
                                    slot.onSlotChanged();
                                    this.detectAndSendChanges();
                                    return ItemStack.EMPTY;
                                }

                                this.detectAndSendChanges();
                            }
                        }
                        else {
                            int maxSize = itemStack.getMaxStackSize();

                            if (maxSize > slot.getSlotStackLimit()) {
                                maxSize = slot.getSlotStackLimit();
                            }

                            final ItemStack tmp = itemStack.copy();

                            if (tmp.getCount() > maxSize) {
                                tmp.setCount(maxSize);
                            }
                            itemStack.shrink(tmp.getCount());
                            slot.putStack(tmp);

                            if (itemStack.getCount() <= 0) {
                                clickSlot.putStack(ItemStack.EMPTY);
                                slot.onSlotChanged();
                                this.detectAndSendChanges();
                                return ItemStack.EMPTY;
                            }

                            this.detectAndSendChanges();
                        }
                    }
                }
            }

            clickSlot.putStack(itemStack != ItemStack.EMPTY ? itemStack.copy() : ItemStack.EMPTY);
        }
        this.detectAndSendChanges();
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith (EntityPlayer playerIn) {

        return playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D && !playerIn.isDead;
    }
}
