package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class MessageBookshelfSync extends SerializableMessage {
    
    public BlockPos[] pos;
    public float[] enchantability;
    public BlockPos tablePos;
    
    public MessageBookshelfSync() {
    }
    
    public MessageBookshelfSync(BlockPos tablePos, BlockPos[] pos, float[] enchantability) {
        this.tablePos = tablePos;
        this.pos = pos;
        this.enchantability = enchantability;
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
        if(context.side != Side.CLIENT) {
            EnchantingPlus.LOG.info("Bookshelf syncing should only run on the Client!");
            return null;
        }
        
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                
                final World world = PlayerUtils.getClientPlayer().world;
                final TileEntityAdvancedTable tile = (TileEntityAdvancedTable) world.getTileEntity(tablePos);
                tile.bookshelves.clear();
                for(int i = 0; i < pos.length; i++) {
                    tile.bookshelves.put(pos[i], enchantability[i]);
                }
            }
        });
        
        
        return null;
    }
}
