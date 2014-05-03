package se.relnah.raspipircx.listener;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;
import se.relnah.raspipircx.service.UtilityService;

public class XpListener extends ListenerAdapter<PircBotX> {
    
    private List<BotUser> userList;
    private ResourceBundle textBundle;
    
    public XpListener(List<BotUser> userList, ResourceBundle textBundle) {
        this.userList = userList;
        this.textBundle = textBundle;
    }
    
    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception {
        
        String nick = event.getUser().getNick();
        
        if (nick.equals(event.getBot().getNick())) {
            return;
        }
        
        Boolean foundUser = false;
        BotUser currentUser = null;

        //Check if new user
        
        currentUser = UtilityService.getUser(event.getUser().getNick(), userList);
        
        if (currentUser != null) {
            foundUser = true;
        }
        
        //New user
        if (!foundUser) {
            BotUser newUsr = new BotUser(nick);
            newUsr.setHostMask(event.getUser().getHostmask());
            addXpToUser(newUsr, 150, event);
            newUsr.setLastJoinedTimestamp(event.getTimestamp());
            userList.add(newUsr);
            
            event.getUser().send().message(textBundle.getString("info.welcome"));
            
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
                
                addXpToUser(currentUser, xp, event);
                
                currentUser.setLastJoinedTimestamp(event.getTimestamp());
            }
            
        }
        

        //Greet joining users.
        String greeting = getGreeting(currentUser, event.getTimestamp());
        
        event.respond(greeting);
        
    }
    

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception{
        
        if (event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }
        
        if (event.getUser().getNick().equals("David_B")) {
            
            //admin actions
        }
        
        //public actions
        
        if (event.getMessage().startsWith(".kudos")) { // Rewards XP. Usage: .kudos <nick>
            String[] param = event.getMessage().split(" ");
            
            BotUser usr = UtilityService.getUser(param[1], userList);
            
            if (usr != null) {
                int xp = 50;
                
                //Add random xp, 0-10
                Random r = new Random();
                xp += r.nextInt(11);
                
                event.respond(usr.getNick() + " fick " + xp + " XP av " + event.getUser().getNick() + " som tack för hjälpen!");
                Thread.sleep(10);
                addXpToUser(usr, xp, event);
            }
        }
        
        
    }
    
    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
            throws Exception {
        //Check if admin
        if (event.getUser().getNick().equals("David_B")) {
            
            //admin commands
            
            //Add xp to user. Usage: .addXp <user> <xp>
            if(event.getMessage().startsWith(".addXp")) {
                String[] param = event.getMessage().split(" ");
                
                event.respond("Add " + param[2] + " to " + param[1]);

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
            greeting += "God morgon ";
        } else {
            greeting += "Hej ";
        }
        
        greeting += "!";
        
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
            event.respond("Ding! " + usr.getNick() + " leveled up and is now level " + usr.getLevel() + ". Congratulations!");
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