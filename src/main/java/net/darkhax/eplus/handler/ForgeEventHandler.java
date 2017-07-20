package net.darkhax.eplus.handler;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.eplus.item.ItemScroll;
import net.darkhax.eplus.loot.EnchantScroll;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ForgeEventHandler {

    @SubscribeEvent
    public void onLootTableLoad (LootTableLoadEvent event) {

        if (ConfigurationHandler.allowScrollLoot && event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {

            final LootPool targetPool = event.getTable().getPool("main");

            if (targetPool != null) {
                targetPool.addEntry(new LootEntryItem(ContentHandler.itemScroll, ConfigurationHandler.scrollWeight, 0, new LootFunction[] { new EnchantScroll(new LootCondition[0]) }, new LootCondition[0], "eplus:dungeon_scroll"));
            }
        }
    }

    @SubscribeEvent
    public void onMobDrops (LivingDropsEvent event) {

        if (ConfigurationHandler.allowScrollDrop && event.getEntity() instanceof IMob && MathsUtils.tryPercentage(ConfigurationHandler.scrollDropChance)) {
            event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, ItemScroll.createScroll(Enchantment.REGISTRY.getRandomObject(Constants.RANDOM))));
        }
    }
}