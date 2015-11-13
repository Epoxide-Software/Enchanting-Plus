package net.epoxide.eplus.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.event.ItemEnchantedEvent;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ForgeEventHandler {
    
    @SubscribeEvent
    public void onItemEnchanted (ItemEnchantedEvent event) {
        
        if (ItemStackUtils.isValidStack(event.stack) && !event.isCanceled())
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