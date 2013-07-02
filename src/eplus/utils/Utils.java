package eplus.utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;

public class Utils
{

    public static String getPlayerCloakURL(EntityPlayer thePlayer)
    {
        Object capeObject = ReflectionHelper.getPrivateValue(EntityPlayer.class, thePlayer, "field_110315_c");

        if (capeObject != null && capeObject.getClass().isAssignableFrom(ThreadDownloadImageData.class))
        {
            ThreadDownloadImageData capeImageData = (ThreadDownloadImageData) capeObject;
            Object textureObject = ReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, capeImageData, "field_110558_f");

            if (textureObject != null && textureObject.getClass().isAssignableFrom(SimpleTexture.class))
            {
                SimpleTexture texture = (SimpleTexture) textureObject;
                Object resourceObject = ReflectionHelper.getPrivateValue(SimpleTexture.class, texture, "field_110568_b");

                if (resourceObject != null && resourceObject.getClass().isAssignableFrom(ResourceLocation.class))
                {
                    ResourceLocation resource = (ResourceLocation) resourceObject;
                    return resource.func_110623_a();
                }
            }

        }
        return "";
    }
}
