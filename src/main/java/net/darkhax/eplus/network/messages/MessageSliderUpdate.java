package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

public class MessageSliderUpdate extends SerializableMessage {
    
    public BlockPos tablePos;
    public EnchantData updatedEnchant;
    
    public MessageSliderUpdate() {
    }
    
    public MessageSliderUpdate(BlockPos tablePos, EnchantData updatedEnchant) {
        this.tablePos = tablePos;
        this.updatedEnchant = updatedEnchant;
    }
    
    /**
     * Called when the message is received and handled. This is where you process the message.
     *
     * @param context The context for the message.
     *
     * @return A message to send as a response.
     */
    @Override
    public IMessage handleMessage(MessageContext context) {
        if(context.side != Side.SERVER) {
            EnchantingPlus.LOG.info("Slider updating should only run on the Server!");
            return null;
        }
        
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                
                final World world = context.getServerHandler().player.world;
                final TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(tablePos);
                List<EnchantData> existingEnchantments = new LinkedList<>();
                boolean containsEnchant = false;
                for(EnchantData data : tile.existingEnchantments) {
                    if(data.enchantment == updatedEnchant.enchantment) {
                        containsEnchant = true;
                        break;
                    }
                }
                if(containsEnchant) {
                    for(EnchantData data : tile.existingEnchantments) {
                        if(data.enchantment == updatedEnchant.enchantment) {
                            existingEnchantments.add(updatedEnchant);
                        } else {
                            existingEnchantments.add(data);
                        }
                    }
                } else {
                    existingEnchantments.addAll(tile.existingEnchantments);
                    existingEnchantments.add(updatedEnchant);
                }
                tile.existingEnchantments = existingEnchantments;
            }
        });
        
        
        return null;
    }
}
