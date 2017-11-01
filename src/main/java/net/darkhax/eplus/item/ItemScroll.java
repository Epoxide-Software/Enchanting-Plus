package net.darkhax.eplus.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.ParticleUtils;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.eplus.client.renderer.color.ScrollColorHandler;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.Content;
import net.darkhax.eplus.libs.EPlusUtils;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScroll extends Item implements IColorfulItem {

    public static final String SCROLL_TAG = "ScrollEnchantment";

    public ItemScroll () {

        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
    }

    public static ItemStack createScroll () {

        return createScroll(Enchantment.REGISTRY.getRandomObject(Constants.RANDOM));
    }

    public static ItemStack createScroll (Enchantment enchantment) {

        return enchantScroll(new ItemStack(Content.itemEnchantedScroll), enchantment);
    }

    public static Enchantment readScroll (ItemStack stack) {

        final NBTTagCompound tag = StackUtils.prepareStackTag(stack);
        return EPlusUtils.getEnchantmentFromId(tag.getString(SCROLL_TAG));
    }

    public static ItemStack enchantScroll (ItemStack stack, Enchantment enchantment) {

        StackUtils.prepareStackTag(stack).setString(SCROLL_TAG, EPlusUtils.getEnchId(enchantment));
        return stack;
    }

    public static boolean isValidScroll (ItemStack stack) {

        final NBTTagCompound tag = StackUtils.prepareStackTag(stack);
        return !stack.isEmpty() && stack.getItem() instanceof ItemScroll && tag.hasKey(SCROLL_TAG) && EPlusUtils.getEnchantmentFromId(tag.getString(SCROLL_TAG)) != null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer player, EnumHand hand) {

        final ItemStack stack = player.getHeldItem(hand);

        if (isValidScroll(stack) && !PlayerHandler.hasEnchantment(player, readScroll(stack))) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public ItemStack onItemUseFinish (ItemStack stack, World worldIn, EntityLivingBase entityLiving) {

        if (entityLiving instanceof EntityPlayer && isValidScroll(stack)) {

            System.out.println(readScroll(stack).getRegistryType().toString());
            PlayerHandler.getPlayerData((EntityPlayer) entityLiving).unlockEnchantment(readScroll(stack));
            if(!worldIn.isRemote )
                stack.shrink(1);
        }

        return stack;
    }

    @Override
    public void onUsingTick (ItemStack stack, EntityLivingBase player, int count) {

        if (count % 4 == 0) {

            final float percent = 1.0f - (float) count / (float) this.getMaxItemUseDuration(stack);
            ParticleUtils.spawnPercentageParticleRing(player.world, EnumParticleTypes.ENCHANTMENT_TABLE, percent, player.posX, player.posY + player.height, player.posZ, 0.0d, 0.0d, 0.0d, 0.15);

            System.out.println(FMLCommonHandler.instance().getEffectiveSide().name() + " " + stack.getTagCompound().toString());
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
    public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> itemList) {

        if (this.isInCreativeTab(tab)) {

            for (final Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {

                if (!ContentHandler.isEnchantmentBlacklisted(enchantment)) {

                    itemList.add(createScroll(enchantment));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, World world, List<String> tip, ITooltipFlag flag) {

        if (isValidScroll(stack)) {

            final Enchantment enchant = readScroll(stack);

            tip.add(ChatFormatting.BLUE + I18n.format("tooltip.eplus.enchantment") + ": " + ChatFormatting.RESET + I18n.format(enchant.getName()));

            if (PlayerHandler.hasEnchantment(PlayerUtils.getClientPlayer(), enchant)) {

                tip.add(ChatFormatting.RED + I18n.format("tooltip.eplus.learned"));
            }
        }

        else {

            tip.add(I18n.format("tooltip.eplus.invalid"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getColorHandler () {

        return ScrollColorHandler.INSTANCE;
    }
}