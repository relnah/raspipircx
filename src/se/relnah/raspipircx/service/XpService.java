/**
 * 
 */
package se.relnah.raspipircx.service;

import java.util.List;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.pojo.UserTitle;

/**
 * @author davbj
 *
 */
public final class XpService {

    /**
     * 
     */
    protected XpService() {}
    private static Logger LOG = LoggerFactory.getLogger(XpService.class);
    
    /**
     * Calculates level based on current XP and current level.
     * @param newXp
     * @param lvl
     * @return int calculated level
     */
    public static int calculateLevel(int newXp, int lvl) {

        //Next level
        int nextLvl = lvl + 1;
        
        int nextLvlReq = (4 * nextLvl) * ((3 * nextLvl) + 45);
        
        while (newXp > nextLvlReq) {
            lvl++;
            nextLvl++;
            nextLvlReq = (4 * nextLvl) * ((3 * nextLvl) + 45);
        }
        
        return lvl;
    }
    
    /**
     * Checks if user is new or existing, checks consecutive days logged in, grants XP for daily login.
     * @param user
     * @param currentUser
     * @param event
     * @return 
     */
    public static BotUser doUserCheck(User user, BotUser currentUser, Event<PircBotX> event, ResourceBundle textBundle, List<BotUser> userList) {
        String nick = UtilityService.cleanNick(user.getNick());
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

            int dayOfWeek = nowDate.dayOfWeek().get();
            //Add xp for first join of the day and if week-day
            if (nowDate.compareTo(lastLoginDate) > 0 && dayOfWeek < 6) {
                
                int xp = 100;
                
                // if last login was yesterday, increase consecDays and multiply xp by days.
                if ( nowDate.minusDays(1).isEqual(lastLoginDate)) {
                    int consecDays = currentUser.increaseConsecutiveDays();
                    xp = xp * consecDays;
                    
                    //Multiply for five days then reset.
                    if (consecDays == 5) {
                        currentUser.setConsecutiveDays(1);
                    }
                } else { //Reset counter if there is a gap
                    currentUser.setConsecutiveDays(1);
                }
                
                joinXp = xp;
                
                currentUser.setLastJoinedTimestamp(event.getTimestamp());
            }
            
        }
        
        //Don't add 0 XP
        if (joinXp > 0) {
            addXpToUser(currentUser, joinXp, event, textBundle);
        }
        return currentUser;
        
    }
    
    /**
     * Adds xp to user and calculates level. Responds with a message if calculated level is greater than current level and sets it as new level.
     * 
     * @param usr
     * @param xp
     * @param event
     */
    public static void addXpToUser(BotUser usr, int xp, Event<PircBotX> event, ResourceBundle textBundle) {
        int newXp = usr.addXp(xp);
        LOG.info("Added " + xp + " XP to user: " + usr.getNick());
        
        int calculatedLevel = XpService.calculateLevel(newXp, usr.getLevel());
        
        if (calculatedLevel > usr.getLevel()) {
            usr.setLevel(calculatedLevel);
            UtilityService.getBotChan(event).send().message(UtilityService.getText(textBundle, "general.levelUp", new String[] {usr.getNick(), usr.getSelectedTitle().getQuoutedTitle(), Integer.toString(usr.getLevel())}));
        }
        
        //Check if user should be awarded titles and award them
        List<UserTitle> awardedTitles = TitleService.awardTitles(usr);
        

        //Notify channel of awarded titles
        if (awardedTitles.size() > 0) {
            Channel chan = UtilityService.getBotChan(event);
            chan.send().message(UtilityService.getText(textBundle, "general.awardedTitleHeading", new String[] {usr.getNick(), usr.getSelectedTitle().getQuoutedTitle()}));
            for (UserTitle userTitle : awardedTitles) {
                chan.send().message(userTitle.getTitle());
            }
            chan.send().message(UtilityService.getText(textBundle, "general.awardedTitleFooter", new String[] {usr.getNick(), usr.getSelectedTitle().getQuoutedTitle()}));
        }
        
    }

}
