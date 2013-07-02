package eplus.helper;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class MathHelper
{

    public static double round(double value, double inc)
    {
        return java.lang.Math.round(value / inc) * inc;
    }
}
