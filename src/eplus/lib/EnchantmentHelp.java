package eplus.lib;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EnchantmentHelp {

    private static Map<String, String> toolTips = new HashMap<String, String>();
    private static List<String> enchantmentBlackList = new ArrayList<String>();
    private static List<Integer> itemBlackList = new ArrayList<Integer>();

    public static String getToolTip(Enchantment enchant) {
        return (toolTips.containsKey(enchant.getName())) ? toolTips.get(enchant.getName()) : "";
    }

    public static boolean putToolTips(Enchantment enchantment, String info) {
        return putToolTips(enchantment.getName(), info);
    }

    public static boolean putToolTips(String enchantment, String info) {
        if (!toolTips.containsKey(enchantment)) {
            toolTips.put(enchantment, info);
            return true;
        }
        return false;
    }

    public static boolean putBlackList(String string) {
        if (!enchantmentBlackList.contains(string)) {
            enchantmentBlackList.add(string);
            return true;
        }
        return false;
    }

    public static boolean isBlackListed(Enchantment enchantment) {
        return enchantmentBlackList.contains(enchantment.getName());
    }

    public static boolean isBlackListed(Item item) {
        return itemBlackList.contains(item.itemID);
    }

    public static boolean putBlackListItem(Integer itemId) {
        if (!itemBlackList.contains(itemId)) {
            itemBlackList.add(itemId);
            return true;
        }
        return false;
    }

    public static void init() {
        putToolTips(Enchantment.unbreaking, "Increases durability");

        //Armour
        putToolTips(Enchantment.thorns, "Chance of dealing damage to mobs or players attacking the wearer");
        putToolTips(Enchantment.aquaAffinity, "Increases underwater mining rate");
        putToolTips(Enchantment.respiration, "Decreases the rate of air loss underwater; increases time between damage while suffocating and drowning");
        putToolTips(Enchantment.projectileProtection, "Protection against damage from projectile entities");
        putToolTips(Enchantment.blastProtection, "Protection against explosion damage; reduces explosion recoil");
        putToolTips(Enchantment.featherFalling, "Protection against fall damage");
        putToolTips(Enchantment.fireProtection, "Protection against fire damage; fire is extinguished faster");
        putToolTips(Enchantment.protection, "Reduces damage from all sources");

        //Weapons
        putToolTips(Enchantment.sharpness, "Extra damage");
        putToolTips(Enchantment.smite, "Extra damage to undead mobs");
        putToolTips(Enchantment.baneOfArthropods, "Extra damage to spiders, cave spiders and silverfish");
        putToolTips(Enchantment.knockback, "Increases knockback");
        putToolTips(Enchantment.fireAspect, "Lights the target on fire");
        putToolTips(Enchantment.looting, "Mobs can drop more loot");

        //Tools
        putToolTips(Enchantment.efficiency, "Faster resource gathering while in use");
        putToolTips(Enchantment.silkTouch, "Mined blocks will drop themselves instead of the item(s) it should drop");
        putToolTips(Enchantment.fortune, "Increases the drop rate of items from blocks");

        //Bows
        putToolTips(Enchantment.power, "Increases damage");
        putToolTips(Enchantment.punch, "Increases knockback");
        putToolTips(Enchantment.flame, "Flaming arrows");
        putToolTips(Enchantment.infinity, "Shooting consumes no arrows");
    }
}
