package eplus.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class NickyHandler
{
    private static BiMap<String, String> nickNames = HashBiMap.create();


    public static void addNick(String username, String nickname)
    {
        nickNames.put(username, nickname);
    }

    public static String getNick(String username)
    {
        return nickNames.get(username);
    }

    public static String getUsername(String nickname)
    {
        return nickNames.inverse().get(nickname);
    }


}
