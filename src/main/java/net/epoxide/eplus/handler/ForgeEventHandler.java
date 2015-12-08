package net.epoxide.eplus.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.darkhax.bookshelf.event.ItemEnchantedEvent;
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.MathsUtils;
import net.darkhax.bookshelf.lib.util.Utilities;

import net.epoxide.eplus.client.gui.GuiModEnchantmentTable;
import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.inventory.EnchantHelper;
import net.epoxide.eplus.item.ItemEnchantedScroll;
import net.epoxide.eplus.modifiers.ScrollModifier;

public class ForgeEventHandler {
    
    @SubscribeEvent
    public void onMobDrops (LivingDropsEvent event) {
        
        if (event.entityLiving instanceof EntityMob && MathsUtils.tryPercentage(EPlusConfigurationHandler.scrollDrop * (event.lootingLevel + 1)))
            event.drops.add(new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.lastTickPosZ, ItemEnchantedScroll.createRandomScroll()));
    }
    
    @SubscribeEvent
    public void onItemEnchanted (ItemEnchantedEvent event) {
        
        if (!event.entityPlayer.capabilities.isCreativeMode && ItemStackUtils.isValidStack(event.stack))
            ItemStackUtils.prepareDataTag(event.stack).setString("enchantedOwnerUUID", event.entityPlayer.getUniqueID().toString());
    }
    
    @SubscribeEvent
    public void onTooltip (ItemTooltipEvent event) {
        
        if (EPlusConfigurationHandler.allowModifierTooltips && ItemStackUtils.isValidStack(event.itemStack)) {
            
            for (ScrollModifier modifier : ContentHandler.modifiers) {
                
                if (ItemStackUtils.areStacksSimilar(event.itemStack, modifier.stack)) {
                    
                    event.toolTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("tooltip.eplus.modifier"));
                    
                    if (modifier.speed != 0.00d) {
                        
                        boolean isPositive = modifier.speed > 0.00d;
                        event.toolTip.add(((isPositive) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + StatCollector.translateToLocal("tooltip.eplus.speed") + ": " + ((isPositive) ? "+" : "") + modifier.speed * 100 + "%");
                    }
                    
                    if (modifier.stability != 0.00d) {
                        
                        boolean isPositive = modifier.stability > 0.00d;
                        event.toolTip.add(((isPositive) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + StatCollector.translateToLocal("tooltip.eplus.stability") + ": " + ((isPositive) ? "+" : "") + modifier.stability * 100 + "%");
                    }
                }
            }
        }
        
        if (Minecraft.getMinecraft().currentScreen instanceof GuiModEnchantmentTable)
            if ((!EPlusConfigurationHandler.allowUnownedModifications && !EnchantHelper.hasRestriction(event.itemStack) && EnchantmentUtils.isStackEnchanted(event.itemStack)) || (EPlusConfigurationHandler.secureItems && EnchantHelper.hasRestriction(event.itemStack) && !EnchantHelper.isValidOwner(event.itemStack, event.entityPlayer)))
                Utilities.wrapStringToListWithFormat(StatCollector.translateToLocal("tooltip.eplus.notowner"), 30, false, event.toolTip, EnumChatFormatting.RED);
    }
    
    @SubscribeEvent
    public void onPlayerClone (PlayerEvent.Clone event) {
        
        PlayerProperties.getProperties(event.entityPlayer).copy(PlayerProperties.getProperties(event.original));
    }
    
    @SubscribeEvent
    public void onEntityConstructing (EntityConstructing event) {
        
        if (event.entity instanceof EntityPlayer && !PlayerProperties.hasProperties((EntityPlayer) event.entity))
            PlayerProperties.setProperties((EntityPlayer) event.entity);
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld (EntityJoinWorldEvent event) {
        
        if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote && PlayerProperties.hasProperties((EntityPlayer) event.entity))
            PlayerProperties.getProperties((EntityPlayer) event.entity).sync();
    }
}