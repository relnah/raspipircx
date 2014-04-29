/**
 * 
 */
package se.relnah.raspipircx.service;

import java.util.List;

import se.relnah.raspipircx.pojo.BotUser;

/**
 * @author davbj
 *
 */
public final class UtilityService {

    protected UtilityService() {}

    /**
     * Gets user matching nick in userList.
     * @param nick
     * @param userList
     * @return BotUser or null if user wasn't found.
     */
    public static BotUser getUser(String nick, List<BotUser> userList) {

        BotUser usr = null;
        
        for (BotUser user : userList) {
            if(nick.equalsIgnoreCase(user.getNick())) {
                usr = user;
                break;
            }
        }
        
        return usr;
        
    }
    
    

}
