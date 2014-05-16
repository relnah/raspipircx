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
public class BotUserXPComparatorRev implements Comparator<BotUser> {

    /**
     * 
     */
    public BotUserXPComparatorRev() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int compare(BotUser user1, BotUser user2) {
        return user2.getXp() - user1.getXp();
    }
    
    

}
