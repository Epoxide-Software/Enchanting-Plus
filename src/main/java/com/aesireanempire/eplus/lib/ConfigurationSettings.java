package com.aesireanempire.eplus.lib;

import java.util.HashMap;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConfigurationSettings
{
    public static final boolean useModDefault = true;
    public static final boolean bookShelvesDefault = true;
    public static final boolean lightDefault = true;
    public static final boolean particlesDefault = true;
    public static final boolean disenchantingDefault = true;
    public static final boolean AllowEnchantDamagedDefault = true;
    public static boolean useMod;
    public static boolean needsBookShelves;
    public static boolean hasLight;
    public static boolean hasParticles;
    public static boolean AllowDisenchanting;
    public static boolean AllowEnchantDamaged;

    public static boolean AllowRepair;
    public static boolean repairDefault = true;

    public static int CostFactor;
    public static int RepairFactor;

    public static int minimumBook;

    public static HashMap<String, Boolean> enchantments = new HashMap<String, Boolean>();

    public static boolean allowDisenUnowned;
}
