package net.darkhax.eplus.handler;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.common.network.packet.PacketSyncUnlockedEnchantments;
import net.darkhax.eplus.libs.Constants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PlayerHandler {

    /**
     * The capability field. Used for checks and references. Initialized when the capability is
     * initialized.
     */
    @CapabilityInject(ICustomData.class)
    public static final Capability<ICustomData> CUSTOM_DATA = null;

    /**
     * Initializes the CustomDataHandler and sets everything up. Should be called in the
     * preInit loading phase.
     */
    public static void init () {

        CapabilityManager.INSTANCE.register(ICustomData.class, new Storage(), Default.class);
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
    }

    /**
     * Used to get easy access to the unlock list.
     *
     * @param player Player to get data for.
     * @return The list of enchantments found.
     */
    public static List<Enchantment> getUnlockedEnchantments (EntityPlayer player) {

        if (player.hasCapability(CUSTOM_DATA, EnumFacing.DOWN)) {
            return player.getCapability(CUSTOM_DATA, EnumFacing.DOWN).getUnlockedEnchantments();
        }

        return null;
    }

    public static boolean knowsEnchantment (EntityPlayer player, Enchantment enchantment) {

        final List<Enchantment> enchants = getUnlockedEnchantments(player);
        return enchants != null && enchants.contains(enchantment);
    }

    /**
     * Unlocks an enchantment for the player.
     *
     * @param player The player.
     * @param enchant The enchantment to unlock.
     */
    public static void unlockEnchantment (EntityPlayer player, Enchantment enchant) {

        if (player.hasCapability(CUSTOM_DATA, EnumFacing.DOWN)) {

            player.getCapability(CUSTOM_DATA, EnumFacing.DOWN).unlockEnchantment(enchant);
            PlayerHandler.syncEnchantmentData(player);
        }
    }

    /**
     * The list of unlocked enchantment to sync.
     *
     * @param player The player to sync enchantments to.
     */
    public static void syncEnchantmentData (EntityPlayer player) {

        if (!player.getEntityWorld().isRemote) {
            EnchantingPlus.network.sendTo(new PacketSyncUnlockedEnchantments(getUnlockedEnchantments(player)), (EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void attachCapabilities (AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Constants.MOD_ID, "playerData"), new Provider());
        }
    }

    @SubscribeEvent
    public void onPlayerClonning (PlayerEvent.Clone event) {

        for (final Enchantment enchantment : getUnlockedEnchantments(event.getOriginal())) {
            unlockEnchantment(event.getEntityPlayer(), enchantment);
        }
    }

    /**
     * Interface for holding various getter and setter methods.
     */
    public static interface ICustomData {

        List<Enchantment> getUnlockedEnchantments ();

        boolean hasEnchantment (Enchantment enchant);

        void unlockEnchantment (Enchantment enchant);

        void lockEnchantment (Enchantment enchant);
    }

    /**
     * Default implementation of the custom data.
     */
    public static class Default implements ICustomData {

        private final List<Enchantment> unlockedEnchants = new ArrayList<>();

        @Override
        public List<Enchantment> getUnlockedEnchantments () {

            return this.unlockedEnchants;
        }

        @Override
        public boolean hasEnchantment (Enchantment enchant) {

            return this.unlockedEnchants.contains(enchant);
        }

        @Override
        public void unlockEnchantment (Enchantment enchant) {

            if (!this.hasEnchantment(enchant)) {
                this.unlockedEnchants.add(enchant);
            }
        }

        @Override
        public void lockEnchantment (Enchantment enchant) {

            this.unlockedEnchants.remove(enchant);
        }
    }

    /**
     * Handles reand/write of custom data.
     */
    public static class Storage implements Capability.IStorage<ICustomData> {

        @Override
        public NBTBase writeNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side) {

            final NBTTagCompound tag = new NBTTagCompound();

            final NBTTagList list = new NBTTagList();

            for (final Enchantment enchant : instance.getUnlockedEnchantments()) {
                list.appendTag(new NBTTagString(enchant.getRegistryName().toString()));
            }

            tag.setTag("unlocked", list);

            return tag;
        }

        @Override
        public void readNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side, NBTBase nbt) {

            final NBTTagCompound tag = (NBTTagCompound) nbt;

            final NBTTagList list = tag.getTagList("unlocked", 8);

            for (int i = 0; i < list.tagCount(); i++) {

                final String id = list.getStringTagAt(i);
                final Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));

                if (enchant != null) {
                    instance.unlockEnchantment(enchant);
                }
                else {
                    EnchantingPlus.printDebugMessage("The enchantment " + id + " does not exist. It will not be loaded.");
                }
            }
        }
    }

    /**
     * Handles all the checks and delegate methods for the capability.
     */
    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        ICustomData instance = CUSTOM_DATA.getDefaultInstance();

        @Override
        public boolean hasCapability (Capability<?> capability, EnumFacing facing) {

            return capability == CUSTOM_DATA;
        }

        @Override
        public <T> T getCapability (Capability<T> capability, EnumFacing facing) {

            return this.hasCapability(capability, facing) ? CUSTOM_DATA.<T> cast(this.instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT () {

            return (NBTTagCompound) CUSTOM_DATA.getStorage().writeNBT(CUSTOM_DATA, this.instance, null);
        }

        @Override
        public void deserializeNBT (NBTTagCompound nbt) {

            CUSTOM_DATA.getStorage().readNBT(CUSTOM_DATA, this.instance, null, nbt);
        }
    }
}
