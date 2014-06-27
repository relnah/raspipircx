package se.relnah.raspipircx;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocketFactory;

import org.joda.time.DateTime;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;

import se.relnah.raspipircx.listener.BotServiceListener;
import se.relnah.raspipircx.listener.CommandListener;
import se.relnah.raspipircx.listener.XpListener;
import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;
import se.relnah.raspipircx.service.XpService;

import com.google.common.collect.ImmutableSortedSet;

public class RaspiPircx {

    private static List<BotUser> userList = new ArrayList<BotUser>();
    private static ResourceBundle textBundle;
    private static PircBotX bot;
    
    public static void main(String[] args) {

        //Get config
        Properties conf = readConfig("./conf/conf.properties");
        
        //Load list of users
        userList = SerializeService.loadGsonUserList();
        /*
        //Code to convert userList to new object
        List<BotUser> newUserList = new ArrayList<BotUser>();
        for (BotUser botUser : userList) {
            BotUser tmpBotUser = new BotUser(botUser.getNick());
            tmpBotUser.setLevel(botUser.getLevel());
            tmpBotUser.setXp(botUser.getXp());
            tmpBotUser.setConsecutiveDays(botUser.getConsecutiveDays());
            tmpBotUser.setHostMask(botUser.getHostMask());
            tmpBotUser.setLastJoinedTimestamp(botUser.getLastJoinedTimestamp());
            tmpBotUser.setTitles(botUser.getTitles());
            tmpBotUser.setChoosenTitleIndex(botUser.getChoosenTitleIndex());
            
            for (int i = 0; i < botUser.getNumKudosGiven(); i++) {
                tmpBotUser.increasKudosGiven();
            }
            
            for (int i = 0; i < botUser.getNumKudosRecieved(); i++) {
                tmpBotUser.increasKudosRecieved();
            }
            
            for (int i = 0; i < botUser.getNumTypedLines(); i++) {
                tmpBotUser.increasLinesTyped();
            }
            
            newUserList.add(tmpBotUser);
        }
        
       userList = newUserList;
       ///End conversion///
       */
        //Load texts
        textBundle = UtilityService.getTextBundle("texts", new Locale("sv", "SE"));
        
        	//Setup this bot
        	Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        	        .setName(conf.getProperty("botNick")) //Set the nick of the bot.
        	        .setLogin(conf.getProperty("login")) //login part of hostmask, eg name:login@host
        	        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        	        .setCapEnabled(true) //Enable CAP features
        	        .addListener(new XpListener(userList, textBundle)) //This class is a listener, so add it to the bots known listeners
        	        .addListener(new CommandListener(userList, textBundle))
        	        .addListener(new BotServiceListener(userList, textBundle))
        	        .setServerHostname(conf.getProperty("server"))
        	        .setServerPort(Integer.parseInt(conf.getProperty("port")))
        	        .setSocketFactory(SSLSocketFactory.getDefault())
        	        .addAutoJoinChannel(conf.getProperty("channel"))
        	        .setEncoding(Charset.forName("ISO-8859-1"))
        	        .buildConfiguration();
        	bot = new PircBotX(configuration);

        	/*
        	//XP curve
        	for (int lvl = 1; lvl <= 60; lvl++) {
        	    int lvlXp = (4 * lvl) * ((3 * lvl) + 45);
        	    System.out.println(lvl + " " + lvlXp);
            }
            System.exit(0);
        	//
        	 * 
        	 */

            //Create timer.
            Timer timer = new Timer(true);
            
            //Create task to save user list
            TimerTask saveUserListTask = new TimerTask() {
                
                @Override
                public void run() {
                    SerializeService.saveGsonUserList(userList);
                }
            };
            
            //Create task to check users
            TimerTask checkUsersTask = new TimerTask() {
				
				@Override
				public void run() {
					checkUsers(bot, textBundle);
					
				}
			};
            
            //Schedule tasks
            timer.schedule(saveUserListTask, 30 * 1000, 5 * 60 * 1000); //Wait 30 seconds and save every 5 min.
            
            DateTime tomorrow = new DateTime().plusDays(1).withTime(8, 0, 0, 0);
            timer.schedule(checkUsersTask, tomorrow.toDate(), 24 * 60 * 60 * 1000); //Start tomorrow at 8 and run every 24h.
        	
        	//Connect to server
        	try {
        	    bot.startBot();
        	} catch (IOException e) {
                SerializeService.saveGsonUserList(userList);
                e.printStackTrace();
            } catch (IrcException e) {
                SerializeService.saveGsonUserList(userList);
                e.printStackTrace();
            }       	
        	
        }
    
    private static void checkUsers(PircBotX bot, ResourceBundle textBundle) {
		
        ImmutableSortedSet<User> users = bot.getUserBot().getChannels().first().getUsers();
        
        for (User user : users) {
            
            //Don't listen to the bot itself
            if (user.getNick().equals(bot.getNick())) {
                continue;
            }
            
            
            //Fake event
            Event<PircBotX> event = new Event<PircBotX>(bot) {
				
				@Override
				public void respond(String arg0) {
					// TODO Auto-generated method stub
					
				}
			};
			
			
            
            BotUser currentUser = UtilityService.getUser(user.getNick(), userList);
            XpService.doUserCheck(user, currentUser, event, textBundle, userList);
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
