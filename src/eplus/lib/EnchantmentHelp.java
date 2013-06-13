package eplus.lib;

import net.minecraft.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EnchantmentHelp {

    private static Map<Enchantment, String> enchantments = new HashMap<Enchantment, String>();

    public static String getInfo(Enchantment enchant) {
        return (enchantments.containsKey(enchant)) ? enchantments.get(enchant) : "";
    }

    public static void put(Enchantment enchantment, String info) {
        enchantments.put(enchantment, info);
    }

    public static void put(String enchantment, String info) {
        for (Enchantment enchant : Enchantment.enchantmentsList) {
            if (enchant.getName().equals(enchantment)) {
                enchantments.put(enchant, info);
            }
        }
    }

    public static void init() {
        put(Enchantment.unbreaking, "Increases durability");

        //Armour
        put(Enchantment.thorns, "Chance of dealing damage to mobs or players attacking the wearer");
        put(Enchantment.aquaAffinity, "Increases underwater mining rate");
        put(Enchantment.respiration, "Decreases the rate of air loss underwater; increases time between damage while suffocating and drowning");
        put(Enchantment.projectileProtection, "Protection against damage from projectile entities");
        put(Enchantment.blastProtection, "Protection against explosion damage; reduces explosion recoil");
        put(Enchantment.featherFalling, "Protection against fall damage");
        put(Enchantment.fireProtection, "Protection against fire damage; fire is extinguished faster");
        put(Enchantment.protection, "Reduces damage from all sources");

        //Weapons
        put(Enchantment.sharpness, "Extra damage");
        put(Enchantment.smite, "Extra damage to undead mobs");
        put(Enchantment.baneOfArthropods, "Extra damage to spiders, cave spiders and silverfish");
        put(Enchantment.knockback, "Increases knockback");
        put(Enchantment.fireAspect, "Lights the target on fire");
        put(Enchantment.looting, "Mobs can drop more loot");

        //Tools
        put(Enchantment.efficiency, "Faster resource gathering while in use");
        put(Enchantment.silkTouch, "Mined blocks will drop themselves instead of the item(s) it should drop");
        put(Enchantment.fortune, "Increases the drop rate of items from blocks");

        //Bows
        put(Enchantment.power, "Increases damage");
        put(Enchantment.punch, "Increases knockback");
        put(Enchantment.flame, "Flaming arrows");
        put(Enchantment.infinity, "Shooting consumes no arrows");

    }
}
