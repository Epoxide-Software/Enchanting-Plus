package net.darkhax.eplus.item;

import java.util.List;
import java.util.Random;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.RenderUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.handler.PlayerHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScroll extends Item {
    
    public ItemScroll() {
        
        this.setUnlocalizedName("eplus.scroll");
        this.setMaxStackSize(16);
        this.setCreativeTab(EnchantingPlus.tabEplus);
    }
    
    public static ItemStack createScroll (Enchantment enchantment) {
        
        final ItemStack stack = new ItemStack(ContentHandler.itemScroll);
        ItemStackUtils.prepareDataTag(stack);
        stack.getTagCompound().setString("ScrollEnchantment", enchantment.getRegistryName().toString());
        return stack;
    }
    
    public static Enchantment readScroll (ItemStack stack) {
        
        return ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(stack.getTagCompound().getString("ScrollEnchantment")));
    }
    
    public static boolean isValidScroll (ItemStack stack) {
        
        ItemStackUtils.prepareDataTag(stack);
        return ItemStackUtils.isValidStack(stack) && stack.getItem() instanceof ItemScroll && stack.getTagCompound().hasKey("ScrollEnchantment") && ForgeRegistries.ENCHANTMENTS.containsKey(new ResourceLocation(stack.getTagCompound().getString("ScrollEnchantment")));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick (ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        
        if (isValidScroll(itemStackIn) && !PlayerHandler.knowsEnchantment(playerIn, readScroll(itemStackIn))) {
            playerIn.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
        
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
    }
    
    @Override
    public ItemStack onItemUseFinish (ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        
        if (!worldIn.isRemote && entityLiving instanceof EntityPlayer && isValidScroll(stack)) {
            
            --stack.stackSize;
            PlayerHandler.unlockEnchantment((EntityPlayer) entityLiving, readScroll(stack));
        }
        
        return stack;
    }
    
    @Override
    public void onUsingTick (ItemStack stack, EntityLivingBase player, int count) {
        
        if (count % 4 == 0) {
            
            final float percent = 1.0f - (float) count / (float) this.getMaxItemUseDuration(stack);
            RenderUtils.spawnPercentageParticleRing(player.worldObj, EnumParticleTypes.ENCHANTMENT_TABLE, percent, player.posX, player.posY + player.height, player.posZ, 0.0d, 0.0d, 0.0d, 0.15);
        }
    }
    
    @Override
    public int getMaxItemUseDuration (ItemStack stack) {
        
        return 60;
    }
    
    @Override
    public EnumAction getItemUseAction (ItemStack stack) {
        
        return EnumAction.BOW;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs tab, List<ItemStack> itemList) {
        
        for (final Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues())
            if (!ContentHandler.isEnchantmentBlacklisted(enchantment))
                itemList.add(createScroll(enchantment));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer reader, List<String> tip, boolean isDebug) {
        
        if (isValidScroll(stack)) {
            
            final Enchantment enchant = readScroll(stack);
            tip.add(ChatFormatting.AQUA + I18n.format("tooltip.eplus.enchantment") + ": " + ChatFormatting.RESET + I18n.format(enchant.getName()));
            
            if (PlayerHandler.knowsEnchantment(reader, enchant))
                tip.add(ChatFormatting.RED + I18n.format("tooltip.eplus.learned"));
        }
        
        else
            tip.add(I18n.format("tooltip.eplus.invalid"));
    }
    
    public static class Function extends LootFunction {
        
        public Function() {
            
            super(new LootCondition[0]);
        }
        
        @Override
        public ItemStack apply (ItemStack stack, Random rand, LootContext context) {
            
            ItemStackUtils.prepareDataTag(stack);
            stack.getTagCompound().setString("ScrollEnchantment", Enchantment.REGISTRY.getRandomObject(Constants.RANDOM).getRegistryName().toString());
            return stack;
        }
    }
}