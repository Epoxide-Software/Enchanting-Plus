package eplus.common;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;
import eplus.client.GuiDisenchantmentItem;
import eplus.client.GuiEnchantmentItem;
import eplus.client.GuiEnchantmentPlus;
import eplus.common.packet.PacketBase;

public class ContainerEnchanting extends Container
{
    public EntityPlayer player;

    public static int bookshelves;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int serverShelves;

    public World gameWorld;
    
    GuiEnchantmentPlus guiEnchantmentPlus;

    public InventoryEnchanting inventoryEnchanting;

    public ContainerEnchanting(EntityPlayer player, World world, int x, int y, int z)
    {
        super();
        this.player = player;
        this.gameWorld = world;
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;

        inventoryEnchanting = new InventoryEnchanting(this, "add", 2);

        addSlotToContainer(new Slot(inventoryEnchanting, 0, 11, 31));
        addSlotToContainer(new Slot(inventoryEnchanting, 1, 11, 57));
        bindPlayerInventory(player.inventory);

        getServerBooks();
    }

    

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int ix = 0; ix < 3; ix++) {
            for (int jx = 0; jx < 9; jx++) {
                addSlotToContainer(new Slot(inventoryPlayer, jx + ix * 9 + 9, 17 + jx * 18, 147 + ix * 18));
            }
        }

        for (int ix = 0; ix < 9; ix++) {
            addSlotToContainer(new Slot(inventoryPlayer, ix, 17 + ix * 18, 205));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }
    
   

    public boolean doEnchant(EntityPlayer player, EnchantmentData[] var1, int var2, int windowid) {

        ItemStack var3 = (ItemStack) this.inventoryItemStacks.get(0);

        if (!this.gameWorld.isRemote && var3 != null) {
            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(-var2);
            }

            for (EnchantmentData var4 : var1) {
                addEnchantment(var4, var3);
            }

            ((EntityPlayerMP) player).sendSlotContents(this, 0, var3);

            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void putStackInSlot(int par1, ItemStack par2ItemStack) {
        super.putStackInSlot(par1, par2ItemStack);
        checkItems(guiEnchantmentPlus);
    }

    public void doDisenchant(EntityPlayer player, EnchantmentData[] var1, int var2) {

        ItemStack var3 = (ItemStack) this.inventoryItemStacks.get(0);

        if (!this.gameWorld.isRemote && var3 != null) {
            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(var2);

            }
            for (EnchantmentData var4 : var1) {
                removeEnchantment(var4, var3);
            }
        }

        PacketDispatcher.sendPacketToPlayer(new Packet103SetSlot(windowId, 0, (ItemStack) this.inventoryItemStacks.get(0)), (Player) player);
    }

    public void addEnchantment(EnchantmentData var1, ItemStack var2) {
        if (var2.stackTagCompound == null) {
            var2.setTagCompound(new NBTTagCompound());
        }
        if (!var2.stackTagCompound.hasKey("ench")) {
            var2.stackTagCompound.setTag("ench", new NBTTagList("ench"));
        }
        NBTTagList var3 = (NBTTagList) var2.stackTagCompound.getTag("ench");
        NBTTagCompound var4 = new NBTTagCompound();
        var4.setShort("id", (short) var1.enchantmentobj.effectId);
        var4.setShort("lvl", (short) var1.enchantmentLevel);
        var4.setShort("bs", (short) bookshelves);
        var3.appendTag(var4);
    }

    public void removeEnchantment(EnchantmentData var1, ItemStack var2) {
        if (var2.stackTagCompound == null) {
            return;
        }
        if (!var2.stackTagCompound.hasKey("ench")) {
            return;
        }
        NBTTagList var3 = new NBTTagList();
        NBTTagList var4 = var2.stackTagCompound.getTagList("ench");
        for (int var5 = 0; var5 < var4.tagCount(); var5++) {
            NBTTagCompound var6 = (NBTTagCompound) var4.tagAt(var5);
            short var7 = var6.getShort("id");
            short var8 = var6.getShort("lvl");
            if (var1.enchantmentobj.effectId != var7 || var1.enchantmentLevel != var8) {
                var3.appendTag(var6.copy());
            }
        }
        if (var3.tagCount() > 0) {
            var2.stackTagCompound.setTag("ench", var3);
        } else {
            HashMap var5 = ReflectionHelper.getPrivateValue(NBTTagCompound.class, var2.stackTagCompound, 0);
            var5.remove("ench");
            ReflectionHelper.setPrivateValue(NBTTagCompound.class, var2.stackTagCompound, var5, 0);
        }
    }

    public int getTotalBookshelves() {
        int var3 = 0;
        if (!this.gameWorld.isRemote) {

            int var4;

            for (var4 = -1; var4 <= 1; ++var4) {
                for (int var5 = -1; var5 <= 1; ++var5) {
                    if ((var4 != 0 || var5 != 0) && this.gameWorld.isAirBlock(this.xPosition + var5, this.yPosition, this.zPosition + var4)
                            && this.gameWorld.isAirBlock(this.xPosition + var5, this.yPosition + 1, this.zPosition + var4)) {
                        if (this.gameWorld.getBlockId(this.xPosition + var5 * 2, this.yPosition, this.zPosition + var4 * 2) == Block.bookShelf.blockID) {
                            ++var3;
                        }

                        if (this.gameWorld.getBlockId(this.xPosition + var5 * 2, this.yPosition + 1, this.zPosition + var4 * 2) == Block.bookShelf.blockID) {
                            ++var3;
                        }

                        if (var5 != 0 && var4 != 0) {
                            if (this.gameWorld.getBlockId(this.xPosition + var5 * 2, this.yPosition, this.zPosition + var4) == Block.bookShelf.blockID) {
                                ++var3;
                            }

                            if (this.gameWorld.getBlockId(this.xPosition + var5 * 2, this.yPosition + 1, this.zPosition + var4) == Block.bookShelf.blockID) {
                                ++var3;
                            }

                            if (this.gameWorld.getBlockId(this.xPosition + var5, this.yPosition, this.zPosition + var4 * 2) == Block.bookShelf.blockID) {
                                ++var3;
                            }

                            if (this.gameWorld.getBlockId(this.xPosition + var5, this.yPosition + 1, this.zPosition + var4 * 2) == Block.bookShelf.blockID) {
                                ++var3;
                            }
                        }
                    }
                }
            }
            return var3;
        } else {
            return bookshelves;
        }

    }

    @Override
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
        super.onCraftGuiClosed(par1EntityPlayer);

        ItemStack itemStack1 = this.inventoryEnchanting.getStackInSlot(0);
        ItemStack itemStack2 = this.inventoryEnchanting.getStackInSlot(1);

        if (itemStack1 != null) {
            par1EntityPlayer.dropPlayerItem(itemStack1);
        }

        if (itemStack2 != null) {
            par1EntityPlayer.dropPlayerItem(itemStack2);
        }

    }

    private void getServerBooks() {
        if (!gameWorld.isRemote) {
            this.bookshelves = getTotalBookshelves();
            Packet250CustomPayload packet = PacketBase.createPacket(99, new byte[] { (byte) this.bookshelves, (byte) 1 });
            PacketDispatcher.sendPacketToAllPlayers(packet);

        }
    }

    public static void setBooks(int books) {
        bookshelves = books;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
        ItemStack var2 = null;
        Slot var3 = (Slot) this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 == 0) {
                if (!this.mergeItemStack(var4, 1, 37, true)) {
                    return null;
                }
            } else {
                if (((Slot) this.inventorySlots.get(0)).getHasStack() || !((Slot) this.inventorySlots.get(0)).isItemValid(var4)) {
                    return null;
                }

                if (var4.hasTagCompound() && var4.stackSize == 1) {
                    ((Slot) this.inventorySlots.get(0)).putStack(var4.copy());
                    var4.stackSize = 0;
                } else if (var4.stackSize >= 1) {
                    ((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(var4.itemID, 1, var4.getItemDamage()));
                    --var4.stackSize;
                }
            }

            if (var4.stackSize == 0) {
                var3.putStack((ItemStack) null);
            } else {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize) {
                return null;
            }

            var3.onPickupFromSlot(par1EntityPlayer, var4);
        }

        return var2;
    }

    public boolean canSetStack(ItemStack var2) {
        // if(mod_EnchantingPlus.isItemEnchantedBook(var2) &&
        // !getSlot(1).getHasStack())
        // {
        // return true;
        // }
        if (getSlot(0).getStack() != null && getSlot(1).getStack() != null) {
            return false;
        } else if (!var2.getItem().isItemTool(var2)) {
            return false;
        } else {
            return true;
        }
    }

    public void doEnchant(ItemStack stack, EntityPlayer player, EnchantmentData[] var1, int var2, int windowid) {
        this.inventoryItemStacks.set(0, stack);
        doEnchant(player, var1, var2, windowid);

    }

    public void doDisenchant(ItemStack stack, EntityPlayer player, EnchantmentData[] var4, int var2) {
        this.inventoryItemStacks.set(0, stack);
        doDisenchant(player, var4, var2);

    }

    public void repair(ItemStack itemStack, EntityPlayer playerEntity, int repairCost) {
        this.inventoryItemStacks.set(0, itemStack);

        ItemStack var3 = (ItemStack) this.inventoryItemStacks.get(0);

        if (!this.gameWorld.isRemote && var3 != null) {

            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(-repairCost);
            }

            ItemStack itemStackCopy = var3.copy();

            var3.setItemDamage(0);

            this.inventoryItemStacks.set(0, itemStackCopy);

        }

    }

    public void transfer(ItemStack stack, ItemStack stack2, EntityPlayerMP playerEntity, int transferCost) {
        this.inventoryItemStacks.set(0, stack);
        this.inventoryItemStacks.set(1, stack2);

        ItemStack itemStack = (ItemStack) this.inventoryItemStacks.get(0);
        ItemStack itemStack1 = (ItemStack) this.inventoryItemStacks.get(1);

        if (!this.gameWorld.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(-transferCost);
            }
            ArrayList<EnchantmentData> list = new ArrayList();

            for (int j = 0; j < itemStack1.getEnchantmentTagList().tagCount(); j++) {
                NBTTagCompound nbt = (NBTTagCompound) itemStack1.getEnchantmentTagList().tagAt(j);
                int k = nbt.getShort("id");
                int l = nbt.getShort("lvl");
                list.add(new EnchantmentData(Enchantment.enchantmentsList[k], l));
            }
            for (EnchantmentData data : list) {
                removeEnchantment(data, itemStack1);
            }

            for (EnchantmentData data : list) {
                addEnchantment(data, itemStack);
            }
        }
    }

    public void checkItems(GuiEnchantmentPlus guiEnchantmentPlus) {
        
        this.guiEnchantmentPlus = guiEnchantmentPlus;

        guiEnchantmentPlus.eIndex = 0;
        guiEnchantmentPlus.dIndex = 0;
        guiEnchantmentPlus.eScroll = 0;
        guiEnchantmentPlus.dScroll = 0;
        guiEnchantmentPlus.possibleEnchantments.clear();
        guiEnchantmentPlus.possibleDisenchantments.clear();
        guiEnchantmentPlus.enchantmentItems.clear();
        guiEnchantmentPlus.disenchantmentItems.clear();
        guiEnchantmentPlus.getIcon("Enchant").enabled = false;
        guiEnchantmentPlus.getIcon("Disenchant").enabled = false;
        if (EnchantingPlus.allowRepair)
            guiEnchantmentPlus.getIcon("Repair").enabled = false;
        if (EnchantingPlus.allowTransfer)
            guiEnchantmentPlus.getIcon("Transfer").enabled = false;

        ItemStack var1 = guiEnchantmentPlus.inventorySlots.getSlot(0).getStack();

        if (var1 != null) {
            ItemStack slot1 = guiEnchantmentPlus.inventorySlots.getSlot(1).getStack();
            if (slot1 != null) {
                boolean var3 = true;
                ArrayList<EnchantmentItemData> var4 = guiEnchantmentPlus.readItem(var1);
                ArrayList<EnchantmentItemData> var5 = guiEnchantmentPlus.readItem(slot1);
                if (var5.size() == 0) {
                    var3 = false;
                } else if (var4.size() > 0 && var5.size() > 0) {
                    for (EnchantmentItemData var6 : var4) {
                        for (EnchantmentItemData var7 : var5) {
                            if (!var7.enchantmentobj.canApplyAtEnchantingTable(var1) || !var6.enchantmentobj.canApplyTogether(var7.enchantmentobj)
                                    || !var7.enchantmentobj.canApplyTogether(var6.enchantmentobj)) {
                                var3 = false;
                            }
                        }
                    }
                } else if (var4.size() == 0 && var5.size() > 0) {
                    for (EnchantmentItemData var6 : var5) {
                        if (!var6.enchantmentobj.canApplyAtEnchantingTable(var1)) {
                            var3 = false;
                        }
                    }
                }
                if (EnchantingPlus.allowTransfer)
                    guiEnchantmentPlus.getIcon("Transfer").enabled = var3 && guiEnchantmentPlus.canPurchase(guiEnchantmentPlus.getTransferCost());
            }
            ArrayList<EnchantmentItemData> list = guiEnchantmentPlus.readItem(var1);
            if (list != null) {
                for (int var2 = 0; var2 < list.size(); var2++) {
                    guiEnchantmentPlus.possibleDisenchantments.add(list.get(var2));
                }
            }
            if (var1.isItemDamaged() && var1.isItemEnchanted()) {
                guiEnchantmentPlus.getIcon("Repair").enabled = guiEnchantmentPlus.canPurchase(guiEnchantmentPlus.getRepairCost());
            }

            for (Enchantment var2 : Enchantment.enchantmentsList) {
                boolean var3 = true;
                if (var2 == null) {
                    continue;
                }
                for (EnchantmentItemData var4 : guiEnchantmentPlus.readItem(var1)) {
                    if (!var2.canApplyTogether(var4.enchantmentobj) || !var4.enchantmentobj.canApplyTogether(var2)) {
                        var3 = false;
                    }
                }
                if (var2.canApplyAtEnchantingTable(var1) && var3 && (var1.isItemEnchantable() || var1.isItemEnchanted())) {
                    guiEnchantmentPlus.possibleEnchantments.add(var2);
                }
            }
            for (int var3 = 0; var3 < guiEnchantmentPlus.possibleEnchantments.size(); var3++) {
                guiEnchantmentPlus.enchantmentItems.add(new GuiEnchantmentItem(guiEnchantmentPlus.possibleEnchantments.get(var3), 35, 16 + var3 * 18));
            }
            for (int var3 = 0; var3 < guiEnchantmentPlus.possibleDisenchantments.size(); var3++) {
                guiEnchantmentPlus.disenchantmentItems.add(new GuiDisenchantmentItem(guiEnchantmentPlus.possibleDisenchantments.get(var3).enchantmentobj,
                        guiEnchantmentPlus.possibleDisenchantments.get(var3).enchantmentLevel, 35, 90 + var3 * 18, guiEnchantmentPlus.possibleDisenchantments
                                .get(var3).shelves));
            }
        }

    }

}
