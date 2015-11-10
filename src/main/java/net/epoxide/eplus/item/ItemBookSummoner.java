package net.epoxide.eplus.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.potion.BuffEffect;
import net.darkhax.bookshelf.potion.BuffHelper;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.handler.ContentHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBookSummoner extends Item {
    
    public ItemBookSummoner() {
        
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setTextureName("eplus:book");
        this.setUnlocalizedName("eplus.book");
    }
    
    public static ItemStack createSummonerItem (int duration, int uses) {
        
        ItemStack stack = new ItemStack(ContentHandler.book);
        ItemStackUtils.prepareDataTag(stack);
        stack.stackTagCompound.setInteger("duration", duration);
        stack.stackTagCompound.setInteger("uses", uses);
        
        return stack;
    }
    
    public static boolean isValid (ItemStack stack) {
        
        return (ItemStackUtils.isValidStack(stack) && stack.hasTagCompound() && stack.stackTagCompound.hasKey("duration") && stack.stackTagCompound.hasKey("uses"));
    }
    
    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player) {
        
        if (isValid(stack)) {
            
            BuffHelper.applyToEntity(player, new BuffEffect(ContentHandler.bookBuff, stack.getTagCompound().getInteger("duration"), 1));
            
            int uses = stack.getTagCompound().getInteger("uses");
            
            if (uses != -1)
                stack.getTagCompound().setInteger("uses", uses - 1);
                
            if (uses != -1 && uses < 1)
                stack.stackSize--;
        }
        
        return stack;
    }
    
    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List tip, boolean isDebug) {
        
        if (isValid(stack)) {
            
            NBTTagCompound tag = stack.getTagCompound();
            BuffEffect effect = new BuffEffect(ContentHandler.bookBuff, tag.getInteger("duration"), 1);
            int uses = tag.getInteger("uses");
            
            tip.add(StatCollector.translateToLocal("tooltip.eplus.duration") + ": " + effect.getDuration());
            tip.add(StatCollector.translateToLocal("tooltip.eplus.uses") + ": " + ((uses == -1) ? EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("tooltip.eplus.unlimited") : uses));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs tab, List list) {
        
        list.add(createSummonerItem(6000, 1));
        list.add(createSummonerItem(2400, 1));
        list.add(createSummonerItem(6000, 5));
        list.add(createSummonerItem(2400, 5));
        list.add(createSummonerItem(6000, -1));
        list.add(createSummonerItem(2400, -1));
    }
}