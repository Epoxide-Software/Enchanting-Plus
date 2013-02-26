package eplus.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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

public class ContainerEnchanting extends Container {
    public EntityPlayer player;

    public static int bookshelves;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int serverShelves;

    public World gameWorld;

    GuiEnchantmentPlus guiEnchantmentPlus;

    public InventoryEnchanting inventoryEnchanting;

    public ContainerEnchanting(EntityPlayer player, World world, int x, int y, int z) {
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

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer)
    {
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
    public boolean canInteractWith(EntityPlayer var1)
    {
        return true;
    }

    public boolean doEnchant(EntityPlayer player, EnchantmentData[] var1, int var2, int windowid)
    {

        ItemStack var3 = (ItemStack) this.inventoryItemStacks.get(0);

        if (!this.gameWorld.isRemote && var3 != null) {

            boolean var5 = var3.itemID == Item.book.itemID;

            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(-var2);
            }

            if (var5) {
                var3.itemID = Item.enchantedBook.itemID;
            }

            for (EnchantmentData var4 : var1) {

                if (var5) {
                    Item.enchantedBook.func_92115_a(var3, var4);
                } else {
                    addEnchantment(var4, var3);
                }

            }

            ((EntityPlayerMP) player).sendSlotContents(this, 0, var3);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
        super.putStackInSlot(par1, par2ItemStack);
        if (par1 == 0 | par1 == 1) // modified by Slash
        	checkItems(guiEnchantmentPlus);
    }

    public void doDisenchant(EntityPlayer player, EnchantmentData[] var1, int var2)
    {
        ItemStack var3 = (ItemStack) this.inventoryItemStacks.get(0);

        if (!this.gameWorld.isRemote && var3 != null) {

            boolean var5 = var3.itemID == Item.enchantedBook.itemID;

            String wordSearch = getEnchantmentSearchWord(var3); // created by Slash
            
            if (!player.capabilities.isCreativeMode) {
                player.addExperienceLevel(var2);
            }

            for (EnchantmentData var4 : var1) {
                removeEnchantment(var4, var3);
            }
            
            // book without any StoredEnchantments needs to be converted to a normal book ID
            if (var5) { // created by Slash
            	//if (var3.getEnchantmentTagList() == null) { // modified by Slash
            	if (var3.stackTagCompound == null || ((NBTTagList)var3.stackTagCompound.getTag(wordSearch) == null)) {
            		var3.itemID = Item.book.itemID;            		
            	}
            }

        }

        PacketDispatcher.sendPacketToPlayer(new Packet103SetSlot(windowId, 0, (ItemStack) this.inventoryItemStacks.get(0)), (Player) player);
    }

    public void addEnchantment(EnchantmentData var1, ItemStack var2)
    {
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

    // items have "ench", but books have "StoredEnchantment"
    private String getEnchantmentSearchWord(ItemStack var2) { // created by Slash
    	String enchantWord = "";
    	
    	if (var2.stackTagCompound != null) {
    		if (var2.stackTagCompound.hasKey("ench")) enchantWord = "ench";
    		if (var2.stackTagCompound.hasKey("StoredEnchantments")) enchantWord = "StoredEnchantments";
    	}
    	
    	return enchantWord;
    }
    
    public void removeEnchantment(EnchantmentData var1, ItemStack var2)
    {
        if (var2.stackTagCompound == null) {
            return;
        }
        
        String wordSearch = getEnchantmentSearchWord(var2); // modified by Slash
        if (wordSearch == "") return; // modified by Slash
        
        //if (!var2.stackTagCompound.hasKey("ench")) return  // modified by Slash

        NBTTagList var3 = new NBTTagList();
        //NBTTagList var4 = var2.stackTagCompound.getTagList("ench");
        NBTTagList var4 = var2.stackTagCompound.getTagList(wordSearch);
        for (int var5 = 0; var5 < var4.tagCount(); var5++) {
            NBTTagCompound var6 = (NBTTagCompound) var4.tagAt(var5);
            short var7 = var6.getShort("id");
            short var8 = var6.getShort("lvl");
            if (var1.enchantmentobj.effectId != var7 || var1.enchantmentLevel != var8) {
                var3.appendTag(var6.copy());
            }
        }
        if (var3.tagCount() > 0) {
            //var2.stackTagCompound.setTag("ench", var3);
        	var2.stackTagCompound.setTag(wordSearch, var3); // modified by Slash
        } else {
            HashMap var5 = ReflectionHelper.getPrivateValue(NBTTagCompound.class, var2.stackTagCompound, 0);
            //var5.remove("ench");
            var5.remove(wordSearch);
            ReflectionHelper.setPrivateValue(NBTTagCompound.class, var2.stackTagCompound, var5, 0);
        }
    }

    public int getTotalBookshelves()
    {
        int var3 = 0;
        if (!this.gameWorld.isRemote) {

            int var4;

            for (var4 = -1; var4 <= 1; ++var4) {
                for (int var5 = -1; var5 <= 1; ++var5) {
                    if ((var4 != 0 || var5 != 0) && this.gameWorld.isAirBlock(this.xPosition + var5, this.yPosition, this.zPosition + var4) && this.gameWorld.isAirBlock(this.xPosition + var5, this.yPosition + 1, this.zPosition + var4)) {
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
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
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

    private void getServerBooks()
    {
        if (!gameWorld.isRemote) {
            this.bookshelves = getTotalBookshelves();
            Packet250CustomPayload packet = PacketBase.createPacket(99, new byte[] { (byte) this.bookshelves, (byte) 1 });
            PacketDispatcher.sendPacketToAllPlayers(packet);

        }
    }

    public static void setBooks(int books)
    {
        bookshelves = books;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotNum)
    {
        ItemStack var2 = null;
        Slot itemSlot = (Slot) this.inventorySlots.get(slotNum);
        int emptySlot = -1;
        
        if (! ((Slot) this.inventorySlots.get(0)).getHasStack() )
        	emptySlot = 0;
        else {
        	if (! ((Slot) this.inventorySlots.get(1)).getHasStack() )
        		emptySlot = 1;
        }
        
        if (itemSlot != null && itemSlot.getHasStack()) {
            ItemStack itemStack = itemSlot.getStack();
            var2 = itemStack.copy();

            if (slotNum == 0 | slotNum == 1) {
                //if (!this.mergeItemStack(itemStack, 1, 37, true)) {
            	if (!this.mergeItemStack(itemStack, 1, 38, true)) { // modified by Slash
                    return null;
                }
            } else {
                if (emptySlot == -1 || ((Slot) this.inventorySlots.get(emptySlot)).getHasStack() || !((Slot) this.inventorySlots.get(emptySlot)).isItemValid(itemStack)) {
                    return null;
                }

                if (itemStack.hasTagCompound() && itemStack.stackSize == 1) {
                    ((Slot) this.inventorySlots.get(emptySlot)).putStack(itemStack.copy());
                    itemStack.stackSize = 0;
                } else if (itemStack.stackSize >= 1) {
                    ((Slot) this.inventorySlots.get(emptySlot)).putStack(new ItemStack(itemStack.itemID, 1, itemStack.getItemDamage()));
                    --itemStack.stackSize;
                }
            }

            if (itemStack.stackSize == 0) {
                itemSlot.putStack((ItemStack) null);
            } else {
                itemSlot.onSlotChanged();
            }

            if (itemStack.stackSize == var2.stackSize) {
                return null;
            }

            itemSlot.onPickupFromSlot(par1EntityPlayer, itemStack);
        }

        return var2;
    }

    public boolean canSetStack(ItemStack var2)
    {
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

    public void doEnchant(ItemStack stack, EntityPlayer player, EnchantmentData[] var1, int var2, int windowid)
    {
        this.inventoryItemStacks.set(0, stack);
        doEnchant(player, var1, var2, windowid);

    }

    public void doDisenchant(ItemStack stack, EntityPlayer player, EnchantmentData[] var4, int var2)
    {
        this.inventoryItemStacks.set(0, stack);
        doDisenchant(player, var4, var2);

    }

    public void repair(ItemStack itemStack, EntityPlayer playerEntity, int repairCost)
    {
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

    public void transfer(ItemStack stack, ItemStack stack2, EntityPlayerMP playerEntity, int transferCost)
    {

        //ItemStack itemStack = (ItemStack) this.inventoryItemStacks.get(0);
        //ItemStack itemStack1 = (ItemStack) this.inventoryItemStacks.get(1);

    	ItemStack itemStack = stack; // modified by Slash
    	ItemStack itemStack1 = stack2; // modified by Slash
    	
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
            
            playerEntity.sendSlotContents(this, 0, itemStack); // modified by Slash
            playerEntity.sendSlotContents(this, 1, itemStack1); // modified by Slash
        }
    }

    public void checkItems(GuiEnchantmentPlus guiEnchantmentPlus)
    {

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
        if (guiEnchantmentPlus.allowDisenchanting) // modified by Slash
        	guiEnchantmentPlus.getIcon("Disenchant").enabled = false;
        if (guiEnchantmentPlus.allowRepair)
            guiEnchantmentPlus.getIcon("Repair").enabled = false;
        if (guiEnchantmentPlus.allowTransfer)
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
                            if (!var7.enchantmentobj.func_92089_a(var1) || !var6.enchantmentobj.canApplyTogether(var7.enchantmentobj) || !var7.enchantmentobj.canApplyTogether(var6.enchantmentobj)) {
                                var3 = false;
                            }
                        }
                    }
                } else if (var4.size() == 0 && var5.size() > 0) {
                    for (EnchantmentItemData var6 : var5) {
                        if (!var6.enchantmentobj.func_92089_a(var1)) {
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

            if (var1.itemID == Item.enchantedBook.itemID) {
                Map var20 = EnchantmentHelper.getEnchantments(var1);
                Iterator var27 = var20.keySet().iterator();

                while (var27.hasNext()) {
                    int id = ((Integer) var27.next()).intValue();
                    Enchantment enchan = Enchantment.enchantmentsList[id];
                    int level = ((Integer) var20.get(Integer.valueOf(id))).intValue();

                    guiEnchantmentPlus.possibleDisenchantments.add(new EnchantmentItemData(enchan, level, bookshelves));
                }
            }

            if (EnchantingPlus.allowRepair && var1.isItemDamaged() && var1.isItemEnchanted()) { // modified by Slash
                guiEnchantmentPlus.getIcon("Repair").enabled = guiEnchantmentPlus.canPurchase(guiEnchantmentPlus.getRepairCost());
            }

            if (var1.getItem().getItemEnchantability() > 0 | !EnchantingPlus.strictEnchant) { // this IF was created by Slash
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
	
	                if (var1.getItem().itemID == Item.book.itemID && var3) {
	                    guiEnchantmentPlus.possibleEnchantments.add(var2);
	                }
	
	                if (!EnchantingPlus.strictEnchant) {
	                    guiEnchantmentPlus.possibleEnchantments.add(var2);
	                } else if (var2.func_92089_a(var1) && var3 && (var1.isItemEnchantable() || var1.isItemEnchanted())) {
	                	guiEnchantmentPlus.possibleEnchantments.add(var2); // modified by slash
	                }
	            }
            }
            for (int var3 = 0; var3 < guiEnchantmentPlus.possibleEnchantments.size(); var3++) {
                guiEnchantmentPlus.enchantmentItems.add(new GuiEnchantmentItem(guiEnchantmentPlus.possibleEnchantments.get(var3), 35, 16 + var3 * 18));
            }
            for (int var3 = 0; var3 < guiEnchantmentPlus.possibleDisenchantments.size(); var3++) {
                guiEnchantmentPlus.disenchantmentItems.add(new GuiDisenchantmentItem(guiEnchantmentPlus.possibleDisenchantments.get(var3).enchantmentobj, guiEnchantmentPlus.possibleDisenchantments.get(var3).enchantmentLevel, 35, 90 + var3 * 18,
                        guiEnchantmentPlus.possibleDisenchantments.get(var3).shelves));
            }
        }

    }

    
}
