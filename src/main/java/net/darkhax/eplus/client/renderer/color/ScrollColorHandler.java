package net.darkhax.eplus.client.renderer.color;

import net.darkhax.eplus.item.ItemScroll;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrollColorHandler implements IItemColor {

    public static final IItemColor INSTANCE = new ScrollColorHandler();

    @Override
    public int colorMultiplier (ItemStack stack, int tintIndex) {

        if (tintIndex == 1 && ItemScroll.isValidScroll(stack)) {

            final Enchantment enchant = ItemScroll.readScroll(stack);

            if (enchant != null) {

                return enchant.getRegistryName().hashCode();
            }
        }

        return 16777215;
    }
}
