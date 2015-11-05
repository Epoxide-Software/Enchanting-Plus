package net.epoxide.eplus.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.client.ProxyClient;
import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.handler.ContentHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEnchantedScroll extends Item {
    
    public static IIcon[] textures = new IIcon[2];
    
    public ItemEnchantedScroll() {
        
        this.setUnlocalizedName("eplus.scroll");
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setHasSubtypes(true);
    }
    
    /**
     * Creates a new ItemStack scroll, with the passed Enchantment written to it.
     *
     * @param ench: The Enchantment to write to the scroll.
     * @return ItemStack: A new ItemStack containing all of the information to be considered a
     *         valid scroll.
     */
    public static ItemStack createScroll (Enchantment ench) {
        
        ItemStack stack = new ItemStack(ContentHandler.scroll);
        ItemStackUtils.prepareDataTag(stack);
        stack.getTagCompound().setInteger("ScrollEnchantment", ench.effectId);
        return stack;
    }
    
    /**
     * Reads an Enchantment from a scroll. If the scroll does not have a valid enchantment,
     * null will be returned.
     *
     * @param stack: The ItemStack to read from.
     * @return Enchantment: The Enchantment that is stored on the scroll. If no Enchantment was
     *         written, or the scroll is invalid, null will be returned.
     */
    public static Enchantment readScroll (ItemStack stack) {
        
        return (isValidScroll(stack) ? Utilities.getEnchantment(stack.getTagCompound().getInteger("ScrollEnchantment")) : null);
    }
    
    /**
     * Checks to see if an ItemStack is a valid scroll. A valid scroll is one which is not
     * null, represents an ItemEnchantedScroll, and has a ScrollEnchantment tag.
     *
     * @param stack: The ItemStack to check.
     * @return boolean: Whether or not the ItemStack passed is considered a valid scroll.
     */
    public static boolean isValidScroll (ItemStack stack) {
        
        ItemStackUtils.prepareDataTag(stack);
        return (ItemStackUtils.isValidStack(stack) && stack.getItem() instanceof ItemEnchantedScroll && stack.getTagCompound().hasKey("ScrollEnchantment"));
    }
    
    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player) {
        
        if (isValidScroll(stack)) {
            
            PlayerProperties props = PlayerProperties.getProperties(player);
            int enchantmentID = readScroll(stack).effectId;
            
            if (!world.isRemote) {
                
                if (!props.unlockedEnchantments.contains(enchantmentID))
                    props.unlockedEnchantments.add(enchantmentID);
                    
                props.sync();
                return stack;
            }
            
            ProxyClient.spawnParticleRing(world, "enchantmenttable", player.posX, player.posY, player.posZ, 0.0d, 0.0d, 0.0d, 0.15);
        }
        
        return stack;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer reader, List tip, boolean isDebug) {
        
        if (isValidScroll(stack))
            tip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.eplus.enchantment") + ": " + EnumChatFormatting.RESET + StatCollector.translateToLocal(readScroll(stack).getName()));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass (int meta, int pass) {
        
        return (pass == 0) ? this.textures[0] : this.textures[1];
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister ir) {
        
        this.textures[0] = ir.registerIcon("eplus:scroll");
        this.textures[1] = ir.registerIcon("eplus:scroll_overlay");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int pass) {
        
        return (isValidScroll(stack) && pass == 1) ? ContentHandler.getEnchantmentColor(readScroll(stack).type.name()) : super.getColorFromItemStack(stack, pass);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs tab, List itemList) {
        
        for (Enchantment enchantment : Utilities.getAvailableEnchantments())
            itemList.add(createScroll(enchantment));
    }
    
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses () {
        
        return true;
    }
}