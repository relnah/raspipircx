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
            
                //Do separate check on verified. Minimize impact of expensive check.
                if (event.getUser().isVerified()) {
                //admin commands
                
                //Save userlist to file
                if(event.getMessage().equals(textBundle.getString("command.admin.save"))) {
                    event.respond(textBundle.getString("command.admin.save.beginSave"));
                    SerializeService.saveUserList(userList);
                    event.respond(textBundle.getString("command.admin.save.saved"));
                } else if (event.getMessage().equalsIgnoreCase(textBundle.getString("command.admin.reloadTexts"))) { //Reloads application texts
                    textBundle = UtilityService.getTextBundle("texts", new Locale("sv", "SE"));
                } else if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.admin.awardTitle").toLowerCase())) {
                    String[] param = event.getMessage().split(" ");
                    BotUser usr = UtilityService.getUser(param[1], userList);
                    
                    //Params from index 2 and onwards are all part of the title.
                    String title = "";
                    for (int i = 2; i < param.length; i++) {
                        title += param[i] + " ";
                    }
                    
                    //Remove trailing space added from last iteration of loop.
                    usr.addTitle(title.trim());
                }
                
            }
        }
        
        //public commands
        
        //Send user info
        if(event.getMessage().equalsIgnoreCase(textBundle.getString("command.myInfo"))) {
            String response = textBundle.getString("command.myInfo.userNotFound");
            
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);
            
            if (usr != null) {
                
                response = UtilityService.getText(textBundle, "command.myInfo.userInfo", new String[] {usr.getNick(), usr.getSelectedTitle(), Integer.toString(usr.getXp()), Integer.toString(usr.getLevel())});
                        
            }
            
            event.respond(response);
        } else if (event.getMessage().equalsIgnoreCase(textBundle.getString("command.help"))) { //Show help message ----------------------------------------------
            event.respond(textBundle.getString("info.help"));
        } else if (event.getMessage().equalsIgnoreCase(textBundle.getString("command.titles"))) { //List awarded titles -------------------------------------------
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
        } else if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.selectTitle").toLowerCase())) { //Selects the active title ---------------------------------
            String[] param = event.getMessage().split(" ");
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);

            //Catch NaN
            try {
                int selectedIndex = Integer.parseInt(param[1]);
                //Make sure to skip OOB
                if (selectedIndex < usr.getTitles().size() && usr.getTitles().size() > -1) {
                    usr.setChoosenTitleIndex(selectedIndex);
                    event.respond(UtilityService.getText(textBundle, "command.selectTitle.confirm", new String[] {usr.getTitles().get(selectedIndex)}));
                    event.respond(UtilityService.getText(textBundle, "command.selectTitle.exampleGreeting", new String[] {usr.getNick(), usr.getSelectedTitle()}));
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
