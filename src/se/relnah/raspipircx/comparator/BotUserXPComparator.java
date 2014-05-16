/**
 * 
 */
package se.relnah.raspipircx.comparator;

import java.util.Comparator;

import se.relnah.raspipircx.pojo.BotUser;

/**
 * @author davbj
 *
 */
public class BotUserXPComparator implements Comparator<BotUser> {

    /**
     * 
     */
    public BotUserXPComparator() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int compare(BotUser user1, BotUser user2) {
        return user1.getXp() - user2.getXp();
    }
    
    

}
