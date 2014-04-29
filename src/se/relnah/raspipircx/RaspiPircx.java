package se.relnah.raspipircx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import se.relnah.raspipircx.listener.CommandListener;
import se.relnah.raspipircx.listener.XpListener;
import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;

public class RaspiPircx {

    private static List<BotUser> userList = new ArrayList<BotUser>();
    public static void main(String[] args) {

        userList = SerializeService.loadUserList();
        	//Setup this bot
        	Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        	        .setName("XP-Bot") //Set the nick of the bot. CHANGE IN YOUR CODE
        	        .setLogin("XPBOT") //login part of hostmask, eg name:login@host
        	        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        	        .setCapEnabled(true) //Enable CAP features
        	        .addListener(new XpListener(userList)) //This class is a listener, so add it to the bots known listeners
        	        .addListener(new CommandListener(userList))
        	        .setServerHostname("leguin.freenode.net")
        	        .setServerPort(6697)
        	        .setSocketFactory(SSLSocketFactory.getDefault())
        	        .addAutoJoinChannel("#dummyTest") //Join the official #pircbotx channel
        	        .buildConfiguration();
        	PircBotX bot = new PircBotX(configuration);

        	//Connect to server
        	try {
        	        bot.startBot();
        	} catch (IOException e) {
                SerializeService.saveUserList(userList);
                e.printStackTrace();
            } catch (IrcException e) {
                SerializeService.saveUserList(userList);
                e.printStackTrace();
            }       	
        	
        }
}
