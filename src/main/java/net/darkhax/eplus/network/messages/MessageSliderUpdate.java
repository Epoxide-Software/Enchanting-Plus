package net.darkhax.eplus.network.messages;

import java.util.LinkedList;
import java.util.List;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSliderUpdate extends SerializableMessage {

    public BlockPos tablePos;
    public EnchantData updatedEnchant;

    public MessageSliderUpdate () {

    }

    public MessageSliderUpdate (BlockPos tablePos, EnchantData updatedEnchant) {

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
    public IMessage handleMessage (MessageContext context) {

        if (context.side != Side.SERVER) {
            EnchantingPlus.LOG.info("Slider updating should only run on the Server!");
            return null;
        }

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask( () -> {

            final World world = context.getServerHandler().player.world;
            final TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(MessageSliderUpdate.this.tablePos);
            final List<EnchantData> existingEnchantments = new LinkedList<>();
            boolean containsEnchant = false;
            for (final EnchantData data1 : tile.existingEnchantments) {
                if (data1.enchantment == MessageSliderUpdate.this.updatedEnchant.enchantment) {
                    containsEnchant = true;
                    break;
                }
            }
            if (containsEnchant) {
                for (final EnchantData data2 : tile.existingEnchantments) {
                    if (data2.enchantment == MessageSliderUpdate.this.updatedEnchant.enchantment) {
                        existingEnchantments.add(MessageSliderUpdate.this.updatedEnchant);
                    }
                    else {
                        existingEnchantments.add(data2);
                    }
                }
            }
            else {
                existingEnchantments.addAll(tile.existingEnchantments);
                existingEnchantments.add(MessageSliderUpdate.this.updatedEnchant);
            }
            tile.existingEnchantments = existingEnchantments;
        });

        return null;
    }
}
