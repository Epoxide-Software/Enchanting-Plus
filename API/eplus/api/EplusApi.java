package eplus.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Map;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EplusApi {
    /**
     * Registers the custom enchantment to be rendered as a tool-tip
     * @param enchantment Enchantment object to be registered
     * @param description Description to be show on the too-tip
     */
    public static void addCustomEnchantmentToolTip(Enchantment enchantment, String description) {
        addCustomEnchantmentToolTip(enchantment.getName(), description);
    }

    /**
     * String version of {@link #addCustomEnchantmentToolTip(net.minecraft.enchantment.Enchantment, String)}
     * @param enchantmentName  Simple name of enchantment
     * @param description Description to be show on the too-tip
     */
    public static void addCustomEnchantmentToolTip(String enchantmentName, String description) {
        FMLInterModComms.sendMessage("eplus", "enchant-tooltip", enchantmentName + ":" + description);
    }

    /**
     * Map version of {@link #addCustomEnchantmentToolTip(net.minecraft.enchantment.Enchantment, String)}
     * @param enchantments map of enchantments
     */
    public static void addCustomEnchantmentToolTip(Map<?, String> enchantments) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList nbtTagList = new NBTTagList();

        for (Object enchant : enchantments.keySet()) {
            String name = "";
            if (Enchantment.class.isAssignableFrom(enchant.getClass())) {
                name = ((Enchantment) enchant).getName();
            } else if (String.class.isAssignableFrom(enchant.getClass())) {
                name = enchant.toString();
            }

            NBTTagCompound tagCompound1 = new NBTTagCompound();
            tagCompound1.setString("Name", name);
            tagCompound1.setString("Description", enchantments.get(enchant));
            nbtTagList.appendTag(tagCompound1);
        }
        tagCompound.setTag("Enchantments", nbtTagList);

        FMLInterModComms.sendMessage("eplus", "enchant-tooltip", tagCompound);
    }
}
