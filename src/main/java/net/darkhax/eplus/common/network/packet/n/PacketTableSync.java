package net.darkhax.eplus.common.network.packet.n;

import com.google.common.collect.Lists;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.client.gui.n.GuiAdvancedTable;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

/**
 * Handled on the client side, used to sync the enchantments from the server to the client
 */
public class PacketTableSync extends SerializableMessage {
    
    //Array because apparently lists mess up with SerializableMessage
    //ResourceLocation because of the *supreme* message system
    public ResourceLocation[] enchantmentsValid;
    
    public EnchantmentData[] enchantmentsCurrent;
    
    public BlockPos pos;
    
    public PacketTableSync() {
    }
    
    public PacketTableSync(TileEntityAdvancedTable tile) {
        this.enchantmentsValid = getLocationsFromEnchants(tile.enchantmentsValid);
        this.enchantmentsCurrent = tile.enchantmentsCurrent.toArray(new EnchantmentData[0]);
        
        this.pos = tile.getPos();
    }
    
    public ResourceLocation[] getLocationsFromEnchants(List<Enchantment> enchants) {
        ResourceLocation[] arr = new ResourceLocation[enchants.size()];
        for(int i = 0; i < enchants.size(); i++) {
            arr[i] = enchants.get(i).getRegistryName();
        }
        return arr;
    }
    
    public Enchantment[] getEnchantsFromLocations(ResourceLocation[] locations) {
        Enchantment[] enchants = new Enchantment[locations.length];
        for(int i = 0; i < locations.length; i++) {
            enchants[i] = Enchantment.REGISTRY.getObject(locations[i]);
        }
        return enchants;
    }
    
    public PacketTableSync(ResourceLocation[] enchantmentsValid, EnchantmentData[] enchantmentsCurrent, BlockPos pos) {
        this.enchantmentsValid = enchantmentsValid;
        this.enchantmentsCurrent = enchantmentsCurrent;
        this.pos = pos;
    }
    
    @Override
    public IMessage handleMessage(MessageContext context) {
        if(context.side == Side.SERVER) {
            EnchantingPlus.LOG.info("Table syncing should only run on the client!");
            return null;
        }
        World world = PlayerUtils.getClientPlayer().world;
        TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(pos);
        tile.enchantmentsValid = new ArrayList<>(Arrays.asList(getEnchantsFromLocations(enchantmentsValid)));
        tile.enchantmentsCurrent = new ArrayList<>(Arrays.asList(enchantmentsCurrent));
        if(Minecraft.getMinecraft().currentScreen instanceof GuiAdvancedTable){
            ((GuiAdvancedTable)Minecraft.getMinecraft().currentScreen).updateLabels();
        }
        return null;
    }
    
}
