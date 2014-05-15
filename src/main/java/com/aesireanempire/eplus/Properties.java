package com.aesireanempire.eplus;

import com.aesireanempire.eplus.helper.PlayerHelper;
import com.aesireanempire.eplus.lib.EnchantmentHelp;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by freyja
 */
public class Properties implements IExtendedEntityProperties
{
    private final EntityPlayer player;

    public Properties(EntityPlayer player)
    {
        this.player = player;
    }

    @Override public void saveNBTData(NBTTagCompound compound)
    {
        ArrayList<Enchantment> playerEnchantments = PlayerHelper.getPlayerEnchantments(player);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        for(Enchantment enchantment : playerEnchantments)
        {
            nbtTagCompound.setBoolean(String.valueOf(enchantment.effectId), true);
        }
        compound.setTag("eplus_tag", nbtTagCompound);
    }

    @Override public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound nbtTagCompound = compound.getCompoundTag("eplus_tag");

        Set<Integer> enchantmentIds = nbtTagCompound.func_150296_c();

        ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();

        for(Integer enchantmentId : enchantmentIds)
        {
            enchantments.add(EnchantmentHelp.getEnchantmentById(enchantmentId));
        }

        PlayerHelper.setPlayerEnchantments(player, enchantments);
    }

    @Override public void init(Entity entity, World world)
    {

    }
}
