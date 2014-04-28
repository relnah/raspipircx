package se.relnah.raspipircx.listener;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.service.SerializeService;

public class XpListener extends ListenerAdapter<PircBotX> {
    
    private List<BotUser> userList;
    
    public XpListener(List<BotUser> userList) {
        this.userList = userList;
    }
    
    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception {
        
        String nick = event.getUser().getNick();
        Boolean foundNick = false;
        BotUser currentUser = null;
        
        //Check if new user
        for (BotUser user : userList) {
            if(nick.equalsIgnoreCase(user.getNick())) {
                foundNick = true;
                currentUser = user;
                break;
            }
        }
        
        //New user
        if (!foundNick) {
            BotUser usr = new BotUser(nick);
            usr.setHostMask(event.getUser().getHostmask());
            addXpToUser(usr, 5, event);
            usr.setLastJoinedTimestamp(event.getTimestamp());
            userList.add(usr);
            
        } else { //Existing user
         
            DateTime now = new DateTime(event.getTimestamp());
            DateTime lastLogin = new DateTime(currentUser.getLastJoinedTimestamp());
            
            LocalDate nowDate = now.toLocalDate();
            LocalDate lastLoginDate = lastLogin.toLocalDate();

            //Add xp for first join of the day
            if (nowDate.compareTo(lastLoginDate) > 0) {
                addXpToUser(currentUser, 1, event);
                currentUser.setLastJoinedTimestamp(event.getTimestamp());
            }
            
        }
        
    }
    
    /**
     * Adds xp to user and calculates level. Responds with a message if calculated level is greater than current level and sets it as new level.
     * 
     * @param usr
     * @param xp
     * @param event
     */
    private void addXpToUser(BotUser usr, int xp, JoinEvent<PircBotX> event) {
        int newXp = usr.addXp(xp);
        
        int calculatedLevel = calculateLevel(newXp, usr.getLevel());
        
        if (calculatedLevel > usr.getLevel()) {
            usr.setLevel(calculatedLevel);
            event.respond("Ding! " + event.getUser().getNick() + " leveled up and is now level " + usr.getLevel() + ". Congratulations!");
        }
        
    }

    /**
     * Calculates level based on current XP and current level.
     * @param newXp
     * @param lvl
     * @return int calculated level
     */
    private int calculateLevel(int newXp, int lvl) {

        int nextLvlReq = (5 * lvl) * ((lvl * 3) + 10);
        
        if (newXp > nextLvlReq) {
            return lvl++;
        } else {
            return lvl;
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception{
        
        
    }
    
    @Override
    public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception {
        SerializeService.saveUserList(userList);
    }
    
}