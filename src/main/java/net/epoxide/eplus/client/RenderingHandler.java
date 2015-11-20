package net.epoxide.eplus.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderingHandler {
    
    @SubscribeEvent
    public void onRenderTick (TickEvent.RenderTickEvent event) {
        
        ProxyClient.notificationHandler.renderNotification();
    }
}