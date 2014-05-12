package se.relnah.raspipircx.listener;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.UserListEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;

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

        currentUser = doUserCheck(event.getUser(), currentUser, event);

        //Greet joining users.
        String greeting = getGreeting(currentUser, event.getTimestamp());
        
        event.respond(greeting);
        
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
            doUserCheck(user, currentUser, event);
        }
        
    }
   
    /**
     * Checks if user is new or existing, checks consecutive days logged in, grants XP for daily login.
     * @param user
     * @param currentUser
     * @param event
     * @return 
     */
    private BotUser doUserCheck(User user, BotUser currentUser, Event<PircBotX> event) {
        String nick = user.getNick();
        int joinXp = 0;
        
        Boolean foundUser = false;

        //Check if new user
        
        if (currentUser != null) {
            foundUser = true;
        }
        
        //New user
        if (!foundUser) {
            currentUser = new BotUser(nick);
            currentUser.setHostMask(user.getHostmask());
            joinXp = 150;
            currentUser.setLastJoinedTimestamp(event.getTimestamp());
            userList.add(currentUser);
            
            user.send().message(UtilityService.getText(textBundle, "info.welcome", new String[] {textBundle.getString("command.kudos")}));
            user.send().message(textBundle.getString("info.welcome_2"));
            user.send().message(UtilityService.getText(textBundle, "info.welcome_3", new String[] {textBundle.getString("command.help")}));
            
        } else { //Existing user
         
            DateTime now = new DateTime(event.getTimestamp());
            DateTime lastLogin = new DateTime(currentUser.getLastJoinedTimestamp());
            
            LocalDate nowDate = now.toLocalDate();
            LocalDate lastLoginDate = lastLogin.toLocalDate();

            //Add xp for first join of the day
            if (nowDate.compareTo(lastLoginDate) > 0) {
                
                int xp = 100;
                
                // if last login was yesterday, increase consecDays and multiply xp by days.
                if ( nowDate.minusDays(1).isEqual(lastLoginDate)) {
                    int consecDays = currentUser.increaseConsecutiveDays();
                    xp = xp * consecDays;
                } else { //Reset counter if there is a gap
                    currentUser.setConsecutiveDays(1);
                }
                
                joinXp = xp;
                
                currentUser.setLastJoinedTimestamp(event.getTimestamp());
            }
            
        }
        
        addXpToUser(currentUser, joinXp, event);
        return currentUser;
        
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception{
        
        //Don't listen to the bot itself
        if (event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }
        
        UtilityService.getUser(event.getUser().getNick(), userList).increasLinesTyped();
        
        
        //Check if admin
        if (UtilityService.isAdmin(event.getUser(), event.getMessage())) {

            //admin actions
        }
        
        //public actions
        
        if (event.getMessage().toLowerCase().startsWith(textBundle.getString("command.kudos").toLowerCase())) { // Rewards XP. Usage: .tack <nick>
            String[] param = event.getMessage().split(" ");
            
            BotUser usr = UtilityService.getUser(param[1], userList);
            
            if (usr != null && !(usr.getNick().equalsIgnoreCase(event.getUser().getNick())) ) {
                int xp = 50;
                
                //Add random xp, 0-10
                Random r = new Random();
                xp += r.nextInt(11);
                
                event.respond(UtilityService.getText(textBundle, "command.kudos.userRewarded", new String[] {usr.getNick(), Integer.toString(xp), event.getUser().getNick()}));
                
                //Increas kudos recieved and given for the users involved.
                usr.increasKudosRecieved();
                UtilityService.getUser(event.getUser().getNick(), userList).increasKudosGiven();
                
                Thread.sleep(10);
                addXpToUser(usr, xp, event);
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
                addXpToUser(usr, Integer.parseInt(param[2]), event);
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
        
        greeting += " " + currentUser.getSelectedTitle();
        
        return greeting;
    
    }

    /**
     * Adds xp to user and calculates level. Responds with a message if calculated level is greater than current level and sets it as new level.
     * 
     * @param usr
     * @param xp
     * @param event
     */
    private void addXpToUser(BotUser usr, int xp, Event<PircBotX> event) {
        int newXp = usr.addXp(xp);
        
        int calculatedLevel = calculateLevel(newXp, usr.getLevel());
        
        if (calculatedLevel > usr.getLevel()) {
            usr.setLevel(calculatedLevel);
            event.respond(UtilityService.getText(textBundle, "general.levelUp", new String[] {usr.getNick(), usr.getSelectedTitle(), Integer.toString(usr.getLevel())}));
        }
        
    }

    /**
     * Calculates level based on current XP and current level.
     * @param newXp
     * @param lvl
     * @return int calculated level
     */
    private int calculateLevel(int newXp, int lvl) {

        int nextLvlReq = (4 * lvl) * ((3 * lvl) + 45);
        
        while (newXp > nextLvlReq) {
            lvl++;
            nextLvlReq = (4 * lvl) * ((3 * lvl) + 45);
        }
        
        return lvl;
    }

    @Override
    public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception {
        SerializeService.saveUserList(userList);
    }
    
}