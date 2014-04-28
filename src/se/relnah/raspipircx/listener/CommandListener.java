/**
 * 
 */
package se.relnah.raspipircx.listener;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;

/**
 * @author davbj
 *
 */
public class CommandListener extends ListenerAdapter<PircBotX> {
    
    private List<BotUser> userList;
    
    public CommandListener(List<BotUser> userList) {
        this.userList = userList;
    }
    

    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
            throws Exception {

        //Check if admin
        if (event.getUser().getNick().equals("David_B")) {
            
            //admin commands
            
            //Save userlist to file
            if(event.getMessage().equals(".save")) {
                event.respond("Savid user list...");
                SerializeService.saveUserList(userList);
                event.respond("Saved!");
            }
            
            //Add xp to user. Usage: .addXp <user> <xp>
            if(event.getMessage().startsWith(".addXp")) {
                String[] param = event.getMessage().split(" ");
                
                event.respond("Add " + param[2] + " to " + param[1]);

                for (BotUser usr : userList) {
                    if (usr.getNick().equals(param[1])) {
                        usr.addXp(Integer.parseInt(param[2]));
                        break;
                    }
                }
                
                
            }
        }
        
        //public commands
        
        //Send user info
        if(event.getMessage().equals(".myInfo")) {
            String response = "Sorry, could not find you in the records...";
                    
            for (BotUser usr : userList) {
                if (usr.getNick().equals(event.getUser().getNick())) {
                    response = "Nick: " + usr.getNick() + " XP: " + usr.getXp() + " Level: " + usr.getLevel();
                    break;
                }
            }
            
            event.respond(response);
        }
        
        
    }

}
