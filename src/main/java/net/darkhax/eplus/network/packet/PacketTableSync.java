package net.darkhax.eplus.network.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handled on the client side, used to sync the enchantments from the server to the client
 */
public class PacketTableSync extends SerializableMessage {

    // Array because apparently lists mess up with SerializableMessage
    // ResourceLocation because of the *supreme* message system
    public ResourceLocation[] enchantmentsValid;

    public EnchantData[] enchantmentsCurrent;
    public EnchantData[] enchantmentsCurrentcache;
    
    
    public BlockPos pos;

    public PacketTableSync () {

    }

    public PacketTableSync (TileEntityAdvancedTable tile) {

        this.enchantmentsValid = this.getLocationsFromEnchants(tile.validEnchantments);
        this.enchantmentsCurrent = new EnchantData[tile.existingEnchantments.size()];
        for(int i = 0; i < tile.existingEnchantments.size(); i++) {
            this.enchantmentsCurrent[i] = tile.existingEnchantments.get(i);
        }
    
        this.enchantmentsCurrentcache = new EnchantData[tile.existingEnchantmentsCache.size()];
        for(int i = 0; i < tile.existingEnchantmentsCache.size(); i++) {
            this.enchantmentsCurrentcache[i] = tile.existingEnchantmentsCache.get(i);
        }
        this.pos = tile.getPos();
    }

    public ResourceLocation[] getLocationsFromEnchants (List<Enchantment> enchants) {

        final ResourceLocation[] arr = new ResourceLocation[enchants.size()];
        for (int i = 0; i < enchants.size(); i++) {
            arr[i] = enchants.get(i).getRegistryName();
        }
        return arr;
    }

    public Enchantment[] getEnchantsFromLocations (ResourceLocation[] locations) {

        final Enchantment[] enchants = new Enchantment[locations.length];
        for (int i = 0; i < locations.length; i++) {
            enchants[i] = Enchantment.REGISTRY.getObject(locations[i]);
        }
        return enchants;
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        if (context.side == Side.SERVER) {
            EnchantingPlus.LOG.info("Table syncing should only run on the client!");
            return null;
        }
        final World world = PlayerUtils.getClientPlayer().world;
        final TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(this.pos);
        tile.validEnchantments = new ArrayList<>(Arrays.asList(this.getEnchantsFromLocations(this.enchantmentsValid)));
        tile.existingEnchantments = new ArrayList<>(Arrays.asList(this.enchantmentsCurrent));
        tile.existingEnchantmentsCache = new ArrayList<>(Arrays.asList(this.enchantmentsCurrentcache));
        tile.updateGui = true;
        return null;
    }

}
