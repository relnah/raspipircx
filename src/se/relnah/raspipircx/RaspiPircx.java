package se.relnah.raspipircx;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocketFactory;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import se.relnah.raspipircx.listener.CommandListener;
import se.relnah.raspipircx.listener.XpListener;
import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;

public class RaspiPircx {

    private static List<BotUser> userList = new ArrayList<BotUser>();
    public static void main(String[] args) {

        //Get config
        Properties conf = readConfig("./conf/conf.properties");
        
        //Load list of users
        userList = SerializeService.loadUserList();
        
        //Load texts
        ResourceBundle textBundle = UtilityService.getTextBundle("texts", new Locale("sv", "SE"));
        
        	//Setup this bot
        	Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        	        .setName(conf.getProperty("botNick")) //Set the nick of the bot.
        	        .setLogin(conf.getProperty("login")) //login part of hostmask, eg name:login@host
        	        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        	        .setCapEnabled(true) //Enable CAP features
        	        .addListener(new XpListener(userList, textBundle)) //This class is a listener, so add it to the bots known listeners
        	        .addListener(new CommandListener(userList, textBundle))
        	        .setServerHostname(conf.getProperty("server"))
        	        .setServerPort(Integer.parseInt(conf.getProperty("port")))
        	        .setSocketFactory(SSLSocketFactory.getDefault())
        	        .addAutoJoinChannel(conf.getProperty("channel"))
        	        .buildConfiguration();
        	PircBotX bot = new PircBotX(configuration);


            //Create timer to save userlist.
            Timer timer = new Timer(true);
            
            TimerTask saveUserListTask = new TimerTask() {
                
                @Override
                public void run() {
                    SerializeService.saveUserList(userList);
                }
            };
            
            //Schedule task
            timer.schedule(saveUserListTask, 30 * 1000, 5 * 60 * 1000);
        	
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
    
    
    
    /**
     * Read the config file
     * @param configFile
     * @return
     */
    private static Properties readConfig(String configFile) {
        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(configFile);
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
        
        return prop;
    }
}
