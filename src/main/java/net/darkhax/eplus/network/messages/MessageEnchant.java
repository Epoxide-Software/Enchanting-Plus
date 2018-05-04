package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

public class MessageEnchant extends SerializableMessage {
    public BlockPos tablePos;
    
    public MessageEnchant() {
    }
    
    public MessageEnchant(BlockPos tablePos) {
        this.tablePos = tablePos;
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
        if (context.side != Side.SERVER) {
            EnchantingPlus.LOG.info("Table enchanting should only run on the Server!");
            return null;
        }
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                final World world = context.getServerHandler().player.world;
                final TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(tablePos);
                tile.enchant();
                
            }
        });
        
        return null;
    }
}
