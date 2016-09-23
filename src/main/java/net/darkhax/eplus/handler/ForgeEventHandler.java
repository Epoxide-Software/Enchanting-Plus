package net.darkhax.eplus.handler;

import net.darkhax.eplus.item.ItemScroll;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ForgeEventHandler {
    
    @SubscribeEvent
    public void onLootTableLoad (LootTableLoadEvent event) {
        
        if (ConfigurationHandler.allowScrollLoot)
            if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
                
                final LootPool pool2 = event.getTable().getPool("pool2");
                
                if (pool2 != null)
                    pool2.addEntry(new LootEntryItem(ContentHandler.itemScroll, ConfigurationHandler.scrollWeight, 0, new LootFunction[] { new ItemScroll.Function() }, new LootCondition[0], "eplus:dungeon_scroll"));
            }
    }
}