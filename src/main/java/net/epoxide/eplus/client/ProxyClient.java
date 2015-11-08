package net.epoxide.eplus.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.epoxide.eplus.client.renderer.tileentity.ArcaneDisenchanterRender;
import net.epoxide.eplus.client.renderer.tileentity.EnchantmentTableRender;
import net.epoxide.eplus.common.ProxyCommon;
import net.epoxide.eplus.tileentity.TileEntityArcaneDisenchanter;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.minecraft.world.World;

import java.util.Random;

public class ProxyClient extends ProxyCommon {

    @Override
    public void registerRenderers () {

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantTable.class, new EnchantmentTableRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcaneDisenchanter.class, new ArcaneDisenchanterRender());

    }

    public static Random rand = new Random();

    public static void spawnParticleRing (World world, String name, double x, double y, double z, double velocityX, double velocityY, double velocityZ, double step) {

        for (double degree = 0.0d; degree < 2 * Math.PI; degree += step)
            world.spawnParticle(name, x + Math.cos(degree), y, z + Math.sin(degree), velocityX, velocityY, velocityZ);
    }

    public static void spawnParticleRing (World world, String name, float percentage, double x, double y, double z, double velocityX, double velocityY, double velocityZ, double step) {

        for (double degree = 0.0d; degree < (2 * Math.PI * percentage); degree += step)
            world.spawnParticle(name, x + Math.cos(degree), y, z + Math.sin(degree), velocityX, velocityY, velocityZ);
    }
}
