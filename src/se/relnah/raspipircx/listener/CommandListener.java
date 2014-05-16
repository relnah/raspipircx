/**
 * 
 */
package se.relnah.raspipircx.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import se.relnah.raspipircx.comparator.BotUserXPComparatorRev;
import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.pojo.UserTitle;
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
        boolean isAdmin = false;
        //Check if admin
        if (UtilityService.isAdmin(event.getUser(), event.getMessage())) {

            isAdmin = true;
            //admin commands
            
            //Save userlist to file
            if(event.getMessage().equals(textBundle.getString("command.admin.save"))) {
                event.respond(textBundle.getString("command.admin.save.beginSave"));
                SerializeService.saveGsonUserList(userList);
                event.respond(textBundle.getString("command.admin.save.saved"));
            } else if (event.getMessage().equalsIgnoreCase(textBundle.getString("command.admin.reloadTexts"))) { //Reloads application texts
                textBundle = UtilityService.getTextBundle("texts", new Locale("sv", "SE"));
            } else if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.admin.awardTitle").toLowerCase())) {
                String[] param = event.getMessage().split(" ");
                BotUser usr = UtilityService.getUser(param[1], userList);
                int levelReq = Integer.parseInt(param[2]);

                //Params from index 3 and onwards are all part of the title.
                String title = "";
                for (int i = 3; i < param.length; i++) {
                    title += param[i] + " ";
                }
                
                //Remove trailing space added from last iteration of loop.
                usr.addCustomTitle(title.trim(), levelReq);
            }
            
        }
        
        //public commands
        
        //Send user info
        if(event.getMessage().equalsIgnoreCase(textBundle.getString("command.myInfo"))) { //Show users info ------------------------------------------------------------------
            String response = textBundle.getString("command.myInfo.userNotFound");
            
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);
            
            if (usr != null) {
                
                response = UtilityService.getText(textBundle, "command.myInfo.userInfo", new String[] {usr.getNick(), usr.getSelectedTitle().getTitle(), Integer.toString(usr.getXp()), Integer.toString(usr.getLevel())});
                        
            }
            
            event.respond(response);
        } else if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.help").toLowerCase())) { //Show help ---------------------------------------

            String[] param = event.getMessage().trim().split(" ");
            Map<String, List<Map<String, String>>> commands = UtilityService.getCommands(textBundle);
            
            //Check if .help or .help <command>
            if (param.length < 2) {
            
                event.respond(UtilityService.getText(textBundle, "info.help", new String[] {textBundle.getString("command.help")}));
    
                //Check if admin
                if (isAdmin) {
                    event.respond(textBundle.getString("info.help.admin"));
                    //admin commands
                    for (Map<String, String> command : commands.get("admin")) {
                        event.respond(command.get("command"));
                    }
                }
                
                event.respond(textBundle.getString("info.help.public"));
                //public commands
                for (Map<String, String> command : commands.get("public")) {
                    event.respond(command.get("command"));
                }
                
            } else {
                
                if (isAdmin) {
                    //admin commands
                    for (Map<String, String> command : commands.get("admin")) {
                        if (command.get("command").equalsIgnoreCase(param[1])){
                            event.respond(UtilityService.getText(textBundle, "command.admin." + command.get("key") + ".help", new String[] {command.get("command")}));
                        }
                    }
                }
                
                //public commands
                for (Map<String, String> command : commands.get("public")) {
                    if (command.get("command").equalsIgnoreCase(param[1])){
                        event.respond(UtilityService.getText(textBundle, "command." + command.get("key") + ".help", new String[] {command.get("command")}));
                    }
                }
                
                
            }

            
        } else if (event.getMessage().equalsIgnoreCase(textBundle.getString("command.titles"))) { //List awarded titles -------------------------------------------

            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);
            event.respond(textBundle.getString("command.titles.heading"));
            
            //Loop and print awarded titles. Mark selected title.
            for (int i = 0; i < usr.getTitles().size(); i++) {
                UserTitle title = usr.getTitles().get(i);
                String response = i + ": " + title.getTitle();
                response += " " + textBundle.getString("command.titles.levelReq") + " " + title.getLevelReq();
                
                if (title.isCustom()) {
                    response += " " + textBundle.getString("command.titles.isCustom");
                }
                
                if (i == usr.getChoosenTitleIndex()) {
                    response += " " + textBundle.getString("command.titles.isSelected");
                }
                event.respond(response);
                
                Thread.sleep(100);
            }
            
            event.respond(UtilityService.getText(textBundle, "command.titles.endInfo", new String[] {textBundle.getString("command.selectTitle")}));
        } else if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.selectTitle").toLowerCase())) { //Selects the active title ---------------------------------
            String[] param = event.getMessage().split(" ");
            BotUser usr = UtilityService.getUser(event.getUser().getNick(), userList);

            //Catch NaN
            try {
                int selectedIndex = Integer.parseInt(param[1]);
                //Make sure to skip OOB
                if (selectedIndex < usr.getTitles().size() && usr.getTitles().size() > -1) {

                    //Check level requirement for title
                    int levelReq = usr.getTitles().get(selectedIndex).getLevelReq();
                    if (levelReq <= usr.getLevel()) {
                        usr.setChoosenTitleIndex(selectedIndex);
                        event.respond(UtilityService.getText(textBundle, "command.selectTitle.confirm", new String[] {usr.getTitles().get(selectedIndex).getTitle()}));
                        event.respond(UtilityService.getText(textBundle, "command.selectTitle.exampleGreeting", new String[] {usr.getNick(), usr.getSelectedTitle().getTitle()}));
                    } else {
                        event.respond(UtilityService.getText(textBundle, "command.selectTitle.notQualified", new String[] {Integer.toString(levelReq)}));
                    }
                    
                } else {
                    event.respond(textBundle.getString("command.selectTitle.wrongIndex"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.respond(textBundle.getString("command.selectTitle.wrongIndex"));
            }

        }
        
        
    }
    
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {

        //Don't listen to the bot itself
        if (event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }
        
        
        //Check if admin
        if (UtilityService.isAdmin(event.getUser(), event.getMessage())) {

        }
        
        if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.listUsers").toLowerCase())) { //List users stats in channel ------------------------------
            
            String[] param = event.getMessage().split(" ");
            
            event.getChannel().send().message(textBundle.getString("command.listUsers.heading"));
            
            Collections.sort(userList, new BotUserXPComparatorRev());
            
            for (BotUser user : userList) {
                String[] textParams = new String[] {user.getNick(), user.getSelectedTitle().getQuoutedTitle(), Integer.toString(user.getLevel())};
                event.getChannel().send().message(UtilityService.getText(textBundle, "command.listUsers.userEntry", textParams));
            }
            
            event.getChannel().send().message(textBundle.getString("command.listUsers.footer"));
        }        
        
    }

}
