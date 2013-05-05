package eplus.handlers;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class NickyChatHandler
{

    @ForgeSubscribe
    public void replaceName(ClientChatReceivedEvent event)
    {
        String rawmessage = event.message;
        String username = rawmessage.substring(rawmessage.indexOf("<") + 1, rawmessage.indexOf(">"));

        String nickName = NickyHandler.getNick(username);
        if(nickName == null) return;

        String message = rawmessage.substring(username.length() + 3);

        event.message = String.format("<%s> %s", nickName, message);
    }
}
