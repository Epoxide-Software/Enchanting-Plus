package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.network.TileEntityMessage;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.util.math.BlockPos;

public class MessageEnchant extends TileEntityMessage {
    
    public MessageEnchant() {
        
    }
    
    public MessageEnchant(BlockPos pos) {
        
        super(pos);
    }

    @Override
    public void getAction () {
        
        if (this.tile instanceof TileEntityAdvancedTable) {
            
            ((TileEntityAdvancedTable) this.tile).enchant();
        }
        
        else {
            
            EnchantingPlus.LOG.error("Attempted to enchant at {} but block was not a valid table!", this.pos);
        }
    }
}
