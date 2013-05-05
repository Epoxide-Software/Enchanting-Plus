package eplus.handlers;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

/**
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class NickyChatHandler
{

    @ForgeSubscribe
    public void replaceName(ServerChatEvent event)
    {
        String nick = NickyHandler.getNick(event.username);
        if(nick == null) return;

        event.line = String.format("<%s> %s", nick, event.message);
    }
}
