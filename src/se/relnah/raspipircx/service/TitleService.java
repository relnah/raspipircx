/**
 * 
 */
package se.relnah.raspipircx.service;

import java.util.ArrayList;
import java.util.List;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.pojo.UserTitle;

/**
 * @author davbj
 *
 */
public final class TitleService {

    /**
     * 
     */
    protected TitleService() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Check if user should be awarded titles and awards the one that the user doesn't already have
     * @param usr
     * @return
     */
    public static List<UserTitle> awardTitles(BotUser usr) {
        // TODO Auto-generated method stub
        // read json with serializeservice
        List<UserTitle> awardToUser = new ArrayList<UserTitle>();
        List<UserTitle> awardedToUser = new ArrayList<UserTitle>();
        
        //Check level titles
        List<UserTitle> levelTitles = SerializeService.loadGsonTitleList();
        
        for (UserTitle userTitle : levelTitles) {
            if (userTitle.getLevelReq() <= usr.getLevel()) {
                awardToUser.add(userTitle);
            }
        }
        
        //TODO Loop and check other types of title lists. Eg. Kudos title list.
        
        //Add titles to user
        for (UserTitle newTitle : awardToUser) {
            Boolean titleExists = false;
            
            //Check if user already has title
            for (UserTitle existingTitle : usr.getTitles()) {
                if (newTitle.getTitle().equalsIgnoreCase(existingTitle.getTitle())) {
                    titleExists = true;
                }
            }
            
            //Add title to user if it's new
            if (!titleExists) {
                usr.addTitle(newTitle);
                awardedToUser.add(newTitle);
            }
        }
        
        return awardedToUser;
    }
    

}
