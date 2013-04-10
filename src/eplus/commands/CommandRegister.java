package eplus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CommandRegister {

    static HashMap<String, List<String>> commands = new HashMap<String, List<String>>();

    static {
        commands.put("useMod", Arrays.asList("true", "false"));
    }

}
