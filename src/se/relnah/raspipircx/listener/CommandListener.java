/**
 * 
 */
package se.relnah.raspipircx.listener;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;

/**
 * @author davbj
 *
 */
public class CommandListener extends ListenerAdapter<PircBotX> {
    
    private List<BotUser> userList;
    private ResourceBundle textBundle;
    
    public CommandListener(List<BotUser> userList, ResourceBundle textBundle) {
        this.userList = userList;
        this.textBundle = textBundle;
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
            } else if (event.getMessage().equals(".reloadTexts")) { //Reloads application texts
                textBundle = UtilityService.getTextBundle("texts", new Locale("sv", "SE"));
            } else if (event.getMessage().startsWith(".awardTitle")) {
                String[] param = event.getMessage().split(" ");
                BotUser usr = UtilityService.getUser(param[1], userList);
                usr.addTitle(param[2]);
            }
            
        }
        
        //public commands
        
        //Send user info
        if(event.getMessage().equals(".minInfo")) {
            String response = textBundle.getString("command.myInfo.userNotFound");
            
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);
            
            if (usr != null) {
                response = "Nick: " + usr.getNick() + " Titel: " + usr.getSelectedTitle() + " XP: " + usr.getXp() + " Level: " + usr.getLevel();
            }
            
            event.respond(response);
        } else if (event.getMessage().equals(".hjälp")) { //Show help message ----------------------------------------------
            event.respond(textBundle.getString("info.help"));
        } else if (event.getMessage().equals(".titlar")) { //List awarded titles -------------------------------------------
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);
            event.respond(textBundle.getString("command.titles.heading"));
            
            //Loop and print awarded titles. Mark selected title.
            for (int i = 0; i < usr.getTitles().size(); i++) {
                String response = i + ": " + usr.getTitles().get(i);
                if (i == usr.getChoosenTitleIndex()) {
                    response += " " + textBundle.getString("command.titles.isSelected");
                }
                event.respond(response);
                
                Thread.sleep(100);
            }
            
            event.respond(textBundle.getString("command.titles.endInfo"));
        } else if (event.getMessage().startsWith(".väljTitel")) { //Selects the active title ---------------------------------
            String[] param = event.getMessage().split(" ");
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);

            //Catch NaN
            try {
                int selectedIndex = Integer.parseInt(param[1]);
                //Make sure to skip OOB
                if (selectedIndex < usr.getTitles().size()) {
                    usr.setChoosenTitleIndex(selectedIndex);
                    event.respond(textBundle.getString("command.selectTitle.confirm") + " " + usr.getTitles().get(selectedIndex));
                    event.respond(textBundle.getString("command.selectTitle.exampleGreeting") + " " + usr.getNick() + " " + usr.getSelectedTitle());
                } else {
                    event.respond(textBundle.getString("command.selectTitle.wrongIndex"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.respond(textBundle.getString("command.selectTitle.wrongIndex"));
            }

        }
        
        
    }

}
