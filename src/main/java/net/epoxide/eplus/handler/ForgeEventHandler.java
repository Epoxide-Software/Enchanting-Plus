package net.epoxide.eplus.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.epoxide.eplus.common.PlayerProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ForgeEventHandler {
    
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