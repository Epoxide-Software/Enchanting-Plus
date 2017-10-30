package net.darkhax.eplus.handler;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.common.network.packet.PacketRequestSync;
import net.darkhax.eplus.common.network.packet.PacketSyncEnchantUnlocks;
import net.darkhax.eplus.libs.EPlusUtils;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerHandler {

    @CapabilityInject(ICustomData.class)
    public static final Capability<ICustomData> CUSTOM_DATA = null;

    public static void init () {

        CapabilityManager.INSTANCE.register(ICustomData.class, new Storage(), Default.class);
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
    }

    public static ICustomData getPlayerData (EntityPlayer player) {

        return player != null && player.hasCapability(CUSTOM_DATA, null) ? player.getCapability(CUSTOM_DATA, null) : null;
    }

    public static boolean hasEnchantment (EntityPlayer player, Enchantment enchant) {

        final ICustomData data = getPlayerData(player);
        return data != null && data.hasEnchantment(enchant);
    }

    @SubscribeEvent
    public void onEntityJoinWorld (EntityJoinWorldEvent event) {

        if (event.getEntity() instanceof EntityPlayer && event.getWorld().isRemote) {

            EnchantingPlus.NETWORK.sendToServer(new PacketRequestSync());
        }
    }

    @SubscribeEvent
    public void attachCapabilities (AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("eplus", "PlayerData"), new Provider((EntityPlayer) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onPlayerClonning (PlayerEvent.Clone event) {

        final ICustomData oldData = getPlayerData(event.getOriginal());
        final ICustomData newData = getPlayerData(event.getEntityPlayer());

        newData.overrideUnlocks(oldData.getUnlockedEnchantments());
        newData.syncData();
    }

    public static interface ICustomData {

        Set<Enchantment> getUnlockedEnchantments ();

        boolean hasEnchantment (Enchantment enchant);

        void unlockEnchantment (Enchantment enchant);

        void lockEnchantment (Enchantment enchant);

        void overrideUnlocks (Set<Enchantment> enchantments);

        void setPlayer (@Nonnull EntityPlayer player);

        @Nullable
        EntityPlayer getPlayer ();

        void syncData ();
    }

    public static class Default implements ICustomData {

        private Set<Enchantment> unlockedEnchants = new HashSet<>();
        private EntityPlayer player;

        @Override
        public Set<Enchantment> getUnlockedEnchantments () {

            return this.unlockedEnchants;
        }

        @Override
        public boolean hasEnchantment (Enchantment enchant) {

            return this.unlockedEnchants.contains(enchant);
        }

        @Override
        public void unlockEnchantment (Enchantment enchant) {

            this.unlockedEnchants.add(enchant);

            if (enchant == null) {

                EnchantingPlus.LOG.warn("Hello null");
            }
        }

        @Override
        public void lockEnchantment (Enchantment enchant) {

            this.unlockedEnchants.remove(enchant);
        }

        @Override
        public void syncData () {

            if (this.getPlayer() instanceof EntityPlayerMP) {

                EnchantingPlus.NETWORK.sendTo(new PacketSyncEnchantUnlocks(this.getUnlockedEnchantments()), (EntityPlayerMP) this.player);
            }
        }

        @Override
        public void setPlayer (EntityPlayer player) {

            this.player = player;
        }

        @Override
        public EntityPlayer getPlayer () {

            return this.player;
        }

        @Override
        public void overrideUnlocks (Set<Enchantment> enchantments) {

            this.unlockedEnchants = enchantments;
        }
    }

    public static class Storage implements Capability.IStorage<ICustomData> {

        @Override
        public NBTBase writeNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side) {

            final NBTTagCompound tag = new NBTTagCompound();

            final NBTTagList list = new NBTTagList();

            for (final Enchantment enchant : instance.getUnlockedEnchantments()) {

                if (enchant != null) {

                    list.appendTag(new NBTTagString(EPlusUtils.getEnchId(enchant)));
                }

                else {

                    EnchantingPlus.LOG.info("Attempted to save null enchantment for {}, it will be discarded.", instance.getPlayer().getDisplayName().getUnformattedText());
                }
            }

            tag.setTag("unlocked", list);

            return tag;
        }

        @Override
        public void readNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side, NBTBase nbt) {

            final NBTTagCompound tag = (NBTTagCompound) nbt;

            final NBTTagList list = tag.getTagList("unlocked", 8);

            for (int i = 0; i < list.tagCount(); i++) {

                final Enchantment enchant = EPlusUtils.getEnchantmentFromId(list.getStringTagAt(i));

                if (enchant != null) {

                    instance.unlockEnchantment(enchant);
                }

                else {

                    EnchantingPlus.LOG.info("Attempted to read null enchantment for {}, it will be discarded.", instance.getPlayer().getDisplayName().getUnformattedText());
                }
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private final ICustomData instance = CUSTOM_DATA.getDefaultInstance();

        public Provider (EntityPlayer player) {

            this.instance.setPlayer(player);
        }

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
