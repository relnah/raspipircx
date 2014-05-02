package se.relnah.raspipircx;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream("./conf/conf.properties");
            prop.load(in);
            in.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        } finally {
            
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        String botNick = prop.getProperty("botNick");
        String login = prop.getProperty("login");
        String channel = prop.getProperty("channel");
        String server = prop.getProperty("server");
        int port = Integer.parseInt(prop.getProperty("port"));
        
        
        
        userList = SerializeService.loadUserList();
        	//Setup this bot
        	Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        	        .setName(botNick) //Set the nick of the bot. CHANGE IN YOUR CODE
        	        .setLogin(login) //login part of hostmask, eg name:login@host
        	        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        	        .setCapEnabled(true) //Enable CAP features
        	        .addListener(new XpListener(userList)) //This class is a listener, so add it to the bots known listeners
        	        .addListener(new CommandListener(userList))
        	        .setServerHostname(server)
        	        .setServerPort(port)
        	        .setSocketFactory(SSLSocketFactory.getDefault())
        	        .addAutoJoinChannel(channel) //Join the official #pircbotx channel
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
