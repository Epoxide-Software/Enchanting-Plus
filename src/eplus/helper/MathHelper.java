package eplus.helper;

/**
 * User: freyja
 * Date: 4/6/13
 * Time: 6:25 AM
 */
public class MathHelper {

    public static double round(double value, double inc)
    {
        return java.lang.Math.round(value / inc) * inc;
    }
}
