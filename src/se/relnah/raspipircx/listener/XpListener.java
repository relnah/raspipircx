package se.relnah.raspipircx.listener;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.UserListEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;
import se.relnah.raspipircx.service.XpService;

import com.google.common.collect.ImmutableSortedSet;

public class XpListener extends ListenerAdapter<PircBotX> {
    
    private List<BotUser> userList;
    private ResourceBundle textBundle;
    
    public XpListener(List<BotUser> userList, ResourceBundle textBundle) {
        this.userList = userList;
        this.textBundle = textBundle;
    }
    
    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception {

        //Don't listen to the bot itself
        if (event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }
        BotUser currentUser = UtilityService.getUser(event.getUser().getNick(), userList);

        currentUser = XpService.doUserCheck(event.getUser(), currentUser, event, textBundle, userList);

        //Greet joining users.
        String greeting = getGreeting(currentUser, event.getTimestamp());
        
        Channel chan = UtilityService.getBotChan(event);
        chan.send().message(greeting);
        
    }
    
    @Override
    public void onUserList(UserListEvent<PircBotX> event) throws Exception {
        
        ImmutableSortedSet<User> users = event.getUsers();
        
        for (User user : users) {
            
            //Don't listen to the bot itself
            if (user.getNick().equals(event.getBot().getNick())) {
                continue;
            }
            
            BotUser currentUser = UtilityService.getUser(user.getNick(), userList);
            XpService.doUserCheck(user, currentUser, event, textBundle, userList);
        }
        
    }
   

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception{
        
        //Don't listen to the bot itself
        if (event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }
        
        //Increas numer of lines typed
        UtilityService.getUser(event.getUser().getNick(), userList).increasLinesTyped();
        
        
        //Check if admin
        if (UtilityService.isAdmin(event.getUser(), event.getMessage())) {

            //admin actions
        }
        
        //public actions
        
        if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.kudos").toLowerCase())) { // Rewards XP. Usage: .tack <nick>
            String[] param = event.getMessage().split(" ");
            String paramNick = UtilityService.removeInvalidCharacters(param[1]);
            BotUser usr = UtilityService.getUser(paramNick, userList);
            
            if (usr != null && !(usr.getNick().equalsIgnoreCase(event.getUser().getNick())) ) {
                int xp = 50;
                
                //Add random xp, 0-10
                Random r = new Random();
                xp += r.nextInt(11);
                
                Channel chan = UtilityService.getBotChan(event);
                chan.send().message(UtilityService.getText(textBundle, "command.kudos.userRewarded", new String[] {usr.getNick(), Integer.toString(xp), event.getUser().getNick()}));
                
                //Increas kudos recieved and given for the users involved.
                usr.increasKudosRecieved();
                UtilityService.getUser(event.getUser().getNick(), userList).increasKudosGiven();
                
                Thread.sleep(10);
                XpService.addXpToUser(usr, xp, event, textBundle);
            }
        }
        
        
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
            throws Exception {
        //Check if admin
        if (UtilityService.isAdmin(event.getUser(), event.getMessage())) {

            //admin commands
            
            //Add xp to user. Usage: .addXp <user> <xp>
            if(event.getMessage().toLowerCase().startsWith(textBundle.getString("command.admin.addXp").toLowerCase())) {
                String[] param = event.getMessage().split(" ");
                
                event.respond(UtilityService.getText(textBundle, "command.admin.addXp.respond", new String[] {param[2], param[1]}));

                BotUser usr = UtilityService.getUser(param[1], userList);
                XpService.addXpToUser(usr, Integer.parseInt(param[2]), event, textBundle);
            }
        }
    }

    /**
     * Gets greeting phrase based on hour of day and user title.
     * @param currentUser
     * @param timestamp
     * @return
     */
    private String getGreeting(BotUser currentUser, long timestamp) {

        String greeting = "";
        DateTime now = new DateTime(timestamp);
        int hour = now.getHourOfDay();
        
        if (hour >= 6 && hour <= 9) {
            greeting = textBundle.getString("general.onJoin.greeting.morning");
        } else {
            greeting = textBundle.getString("general.onJoin.greeting.default");
        }
        
        greeting += " " + currentUser.getNick() + " " + currentUser.getSelectedTitle().getQuoutedTitle() + "!";
        
        return greeting;
    
    }


    @Override
    public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception {
        SerializeService.saveGsonUserList(userList);
    }
    
}