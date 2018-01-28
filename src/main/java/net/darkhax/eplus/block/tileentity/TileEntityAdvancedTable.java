package net.darkhax.eplus.block.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.darkhax.eplus.network.packet.PacketTableSync;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityAdvancedTable extends TileEntityWithBook implements IInteractionObject {

    public ItemStackHandlerEnchant inventory;

    public TileEntityAdvancedTable () {

        this.inventory = new ItemStackHandlerEnchant(this, 1);
    }

    public List<Enchantment> validEnchantments = new ArrayList<>();

    public List<EnchantmentData> existingEnchantments = new ArrayList<>();

    public List<BlockPos> enchantmentTables = new ArrayList<>();
    /**
     * map containing the position of the bookshelves around the table and their enchantability (since it can be more than 1)
     */
    public Map<BlockPos, Float> bookshelves = new HashMap<>();
    
    /**
     * tells the gui to update. Client side only
     */
    public boolean updateGui = false;

    public void updateItem () {

        final ItemStack stack = this.inventory.getStackInSlot(0);
        this.validEnchantments.clear();
        this.existingEnchantments.clear();

        if (!stack.isEmpty()) {
            if (stack.isItemEnchantable() || stack.isItemEnchanted()) {
                for (final Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                    this.existingEnchantments.add(new EnchantData(entry.getKey(), entry.getValue()));
                }
                this.validEnchantments.addAll(this.getEnchantmentsForItem(stack));

            }
        }
        if (!this.world.isRemote) {
            EnchantingPlus.NETWORK.sendToAllAround(new PacketTableSync(this), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 128D));
            this.markDirty();
        }
    }

    private List<Enchantment> getEnchantmentsForItem (ItemStack stack) {

        final List<Enchantment> enchList = new ArrayList<>();

        for (final Enchantment enchantment : Enchantment.REGISTRY) {

            if (stack.isItemEnchanted()) {

                if (enchantment.canApply(stack) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {

                    enchList.add(enchantment);
                }
            }

            else {

                if (enchantment.type.canEnchantItem(stack.getItem()) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {

                    enchList.add(enchantment);
                }
            }
        }
        return enchList;
    }

    public int getCurrentLevelForEnchant (Enchantment enchant) {

        for (final EnchantmentData data : this.existingEnchantments) {
            if (data.enchantment.getRegistryName().equals(enchant.getRegistryName())) {
                return data.enchantmentLevel;
            }
        }
        return 0;
    }
    public void searchForShelves() {
        //TODO this needs to run everytime that a tile changes withing a 2 block radius around the table
        final int x = this.getPos().getX();
        final int y = this.getPos().getY();
        final int z = this.getPos().getZ();
        final Map<BlockPos, Float> tables = new HashMap<>();
        for(int xOff = -1; xOff < 2; xOff++) {
            for(int zOff = -1; zOff < 2; zOff++) {
                for(int yOff = 0; yOff < 2; yOff++) {
                    
                    if(xOff == 0 && zOff == 0) {
                        continue;
                    }
                    
                    final BlockPos pos = new BlockPos(x + xOff, y + yOff, z + zOff);
                    
                    if(this.world.isAirBlock(pos)) {
                        BlockPos pos1 = new BlockPos(x + xOff * 2, y + yOff, z + zOff);
                        float enchantPower = ForgeHooks.getEnchantPower(this.world, pos1);
                        if(enchantPower > 0) {
                            tables.put(pos1, enchantPower);
                        }
                        pos1 = new BlockPos(x + xOff, y + yOff, z + zOff * 2);
                        enchantPower = ForgeHooks.getEnchantPower(this.world, pos1);
                        if(enchantPower > 0) {
                            tables.put(pos1, enchantPower);
                        }
                        pos1 = new BlockPos(x + xOff * 2, y + yOff, z + zOff * 2);
                        enchantPower = ForgeHooks.getEnchantPower(this.world, pos1);
                        if(enchantPower > 0) {
                            tables.put(pos1, enchantPower);
                        }
                    }
                }
            }
        }
        this.bookshelves = tables;
        if(!world.isRemote) {
            BlockPos[] bPos = new BlockPos[bookshelves.size()];
            float[] enchants = new float[bookshelves.size()];
            int count = 0;
            for(Map.Entry<BlockPos, Float> entry : bookshelves.entrySet()) {
                bPos[count] = entry.getKey();
                enchants[count] = entry.getValue();
                count++;
            }
            EnchantingPlus.NETWORK.sendToAllAround(new MessageBookshelfSync(getPos(), bPos, enchants), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
            markDirty();
        }
    }

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {

        return new ContainerEnchantment(playerInventory, this.world, this.pos);
    }

    @Override
    public ITextComponent getDisplayName () {

        return new TextComponentString(this.getName());
    }

    @Override
    public String getGuiID () {

        return "eplus:enchanting_table";
    }

    @Override
    public String getName () {

        return "container.enchant";
    }

    @Override
    public boolean hasCustomName () {

        return false;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("inventory", this.inventory.serializeNBT());
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.inventory.deserializeNBT((NBTTagCompound) dataTag.getTag("inventory"));
    }

    @Override
    public boolean hasCapability (Capability<?> capability, @Nullable EnumFacing facing) {

        return this.getCapability(capability, facing) != null || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability (Capability<T> capability, @Nullable EnumFacing facing) {

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }
}