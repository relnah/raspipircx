package se.relnah.raspipircx;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import se.relnah.raspipircx.listener.HelloListener;

public class RaspiPircx {
        public static void main(String[] args) throws Exception {

        	//Setup this bot
        	Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        	        .setName("Relnah_Servant") //Set the nick of the bot. CHANGE IN YOUR CODE
        	        .setLogin("RelnahServant") //login part of hostmask, eg name:login@host
        	        .setAutoNickChange(true) //Automatically change nick when the current one is in use
        	        //.setCapEnabled(true) //Enable CAP features
        	        .addListener(new HelloListener()) //This class is a listener, so add it to the bots known listeners
        	        .setServerHostname("se.quakenet.org")
        	        .addAutoJoinChannel("#ist-ku-se") //Join the official #pircbotx channel
        	        .buildConfiguration();
        	PircBotX bot = new PircBotX(configuration);

        	//Connect to server
        	try {
        	        bot.startBot();
        	} catch (Exception ex) {
        	         ex.printStackTrace();
        	}        	
        	
        	
        	
        }
}
