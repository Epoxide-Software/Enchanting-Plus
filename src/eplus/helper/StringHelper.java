package eplus.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class StringHelper
{

    public static String keySetToString(Set<String> strings)
    {
        return listToString(Arrays.asList(strings.toArray(new String[strings.size()])));
    }

    public static String listToString(List<String> list)
    {
        String ret = "[ ";
        for (int i = 0; i < list.size(); i++)
        {
            ret += list.get(i);

            if (i + 1 <= list.size() - 1)
            {
                ret += " | ";
            } else
            {
                ret += " ";
            }
        }
        ret += "]";
        return ret;
    }
}
