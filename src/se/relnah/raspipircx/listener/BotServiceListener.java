/**
 * 
 */
package se.relnah.raspipircx.listener;

import java.util.List;
import java.util.ResourceBundle;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.UtilityService;

/**
 * @author davbj
 *
 */
public class BotServiceListener extends ListenerAdapter<PircBotX> {

    
    private List<BotUser> userList;
    private ResourceBundle textBundle;
    
    public BotServiceListener(List<BotUser> userList, ResourceBundle textBundle) {
        this.userList = userList;
        this.textBundle = textBundle;
    }
    
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        
        String msg = event.getMessage();
        
        for (User usr : event.getChannel().getUsers()) {
            if (msg.toLowerCase().contains(usr.getNick().toLowerCase())) {
                BotUser botUser = UtilityService.getUser(usr.getNick(), userList);
                if ("true".equalsIgnoreCase(botUser.getSetting("notification"))) {
                    usr.send().message(textBundle.getString("general.userNotification"));
                }
            }
        }
        
    }

}
