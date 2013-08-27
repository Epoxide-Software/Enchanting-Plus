package eplus.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class EplusApi {
	/**
	 * Registers the custom enchantment to be rendered as a tool-tip
	 * 
	 * @param enchantment
	 *            Enchantment object to be registered
	 * @param description
	 *            Description to be show on the too-tip
	 */
	public static void addCustomEnchantmentToolTip(Enchantment enchantment,
			String description) {
		addCustomEnchantmentToolTip(enchantment.getName(), description);
	}

	/**
	 * String version of
	 * {@link #addCustomEnchantmentToolTip(net.minecraft.enchantment.Enchantment, String)}
	 * 
	 * @param enchantmentName
	 *            Simple name of enchantment
	 * @param description
	 *            Description to be show on the too-tip
	 */
	public static void addCustomEnchantmentToolTip(String enchantmentName,
			String description) {
		FMLInterModComms.sendMessage("eplus", "enchant-tooltip",
				enchantmentName + ":" + description);
	}

	/**
	 * Map version of
	 * {@link #addCustomEnchantmentToolTip(net.minecraft.enchantment.Enchantment, String)}
	 * 
	 * @param enchantments
	 *            map of enchantments
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

	/**
	 * Add an enchantment to not show up in the enchanting interface
	 * 
	 * @param enchantment
	 *            Enchantment object to blacklist
	 */
	public static void addEnchantmentToBlackList(Enchantment enchantment) {
		addEnchantmentToBlackList(enchantment.getName());
	}

	/**
	 * String version of
	 * {@link #addEnchantmentToBlackList(net.minecraft.enchantment.Enchantment)}
	 * 
	 * @param enchantmentName
	 *            Name of enchantment to blacklist
	 */
	public static void addEnchantmentToBlackList(String enchantmentName) {
		FMLInterModComms.sendMessage("eplus", "blacklist-enchantment",
				enchantmentName);
	}

	/**
	 * List version of
	 * {@link #addEnchantmentToBlackList(net.minecraft.enchantment.Enchantment)}
	 * 
	 * @param enchantments
	 *            List of enchantments to blacklist
	 */
	public static void addEnchantmentToBlackList(List<?> enchantments) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagList nbtTagList = new NBTTagList();

		for (Object enchant : enchantments) {
			String name = "";
			if (Enchantment.class.isAssignableFrom(enchant.getClass())) {
				name = ((Enchantment) enchant).getName();
			} else if (String.class.isAssignableFrom(enchant.getClass())) {
				name = enchant.toString();
			}

			NBTTagCompound tagCompound1 = new NBTTagCompound();
			tagCompound1.setString("Name", name);
			nbtTagList.appendTag(tagCompound1);
		}
		tagCompound.setTag("Enchantments", nbtTagList);

		FMLInterModComms.sendMessage("eplus", "blacklist-enchantment",
				tagCompound);
	}

	/**
	 * Adds item to being able not to be used in the enchanting interface
	 * 
	 * @param item
	 *            Item object to blacklist
	 */
	public static void addItemToBlackList(Item item) {
		addItemToBlackList(item.itemID);
	}

	/**
	 * Integer version of {@link #addItemToBlackList(net.minecraft.item.Item)}
	 * 
	 * @param itemId
	 *            Item Id to blacklist
	 */
	public static void addItemToBlackList(Integer itemId) {
		FMLInterModComms.sendMessage("eplus", "blacklist-item",
				String.valueOf(itemId));
	}

	/**
	 * List version of {@link #addItemToBlackList(net.minecraft.item.Item)}
	 * 
	 * @param items
	 *            List of items to blacklist
	 */
	public static void addItemToBlackList(List<?> items) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagList nbtTagList = new NBTTagList();

		for (Object item : items) {
			int itemId = 0;
			if (Item.class.isAssignableFrom(item.getClass())) {
				itemId = ((Item) item).itemID;
			} else if (Integer.class.isAssignableFrom(item.getClass())) {
				itemId = (int) item;
			}

			NBTTagCompound tagCompound1 = new NBTTagCompound();
			tagCompound1.setString("itemId", String.valueOf(itemId));
			nbtTagList.appendTag(tagCompound1);
		}
		tagCompound.setTag("items", nbtTagList);

		FMLInterModComms.sendMessage("eplus", "blacklist-item", tagCompound);
	}

	static Class<?> EnchantmentHelp;
	static Method ToolTip;

	/**
	 * Gets the string description for an enchantment
	 * 
	 * @param enchantment
	 *            Enchantment object to get
	 * @return String description
	 */
	public static String getEnchantmentToolTip(Enchantment enchantment) {
		try {
			if (EnchantmentHelp == null) {
				EnchantmentHelp = Class.forName("eplus.lib.EnchantmentHelp");
			}

			if (ToolTip == null) {
				ToolTip = EnchantmentHelp.getMethod("getToolTip",
						net.minecraft.enchantment.Enchantment.class);
			}

			return String.valueOf(ToolTip
					.invoke(getMainInstance(), enchantment));

		} catch (Exception e) {
			return "";
		}
	}

	static Class<?> EnchantingPlus;
	static Field EnchantingPlusInstance;
	static Object EnchantingPlusObject;

	/**
	 * Gets the instance of Enchanting plus currently running.
	 * 
	 * @return EnchantingPlus object or null
	 */
	public static Object getMainInstance() {
		try {
			if (EnchantingPlus == null) {
				EnchantingPlus = Class.forName("eplus.EnchantingPlus");
			}
			if (EnchantingPlusInstance == null) {
				EnchantingPlusInstance = EnchantingPlus
						.getField("eplus.EnchantingPlus.INSTANCE");
			}
			if (EnchantingPlusObject == null) {
				EnchantingPlusObject = EnchantingPlusInstance.get(null);
			}

			return EnchantingPlusObject;

		} catch (Exception e) {
			return null;
		}
	}
}
