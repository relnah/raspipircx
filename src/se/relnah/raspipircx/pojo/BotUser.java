/**
 * 
 */
package se.relnah.raspipircx.pojo;

import java.io.Serializable;

/**
 * @author davbj
 *
 */
public class BotUser implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7162503873980220558L;

    private String nick;
    private String hostMask;
    private int xp;
    private int level;
    private long lastJoinedTimestamp;
    
    
    /**
     * @param nick
     */
    public BotUser(String nick) {
        this.nick = nick;
        this.xp = 0;
        this.level = 0;
    }

    /**
     * @param xp
     * @return
     */
    public int addXp(int xp) {
        
        this.xp += xp;
        return this.xp;
    }
    
    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }
    /**
     * @param nick the nick to set
     */
    public void setNick(String nick) {
        this.nick = nick;
    }
    /**
     * @return the hostMask
     */
    public String getHostMask() {
        return hostMask;
    }
    /**
     * @param hostMask the hostMask to set
     */
    public void setHostMask(String hostMask) {
        this.hostMask = hostMask;
    }
    /**
     * @return the xp
     */
    public int getXp() {
        return xp;
    }
    /**
     * @param xp the xp to set
     */
    public void setXp(int xp) {
        this.xp = xp;
    }
    /**
     * @return the level
     */

    /**
     * @return the lastJoinedTimestamp
     */
    public long getLastJoinedTimestamp() {
        return lastJoinedTimestamp;
    }
    /**
     * @param lastJoinedTimestamp the lastJoinedTimestamp to set
     */
    public void setLastJoinedTimestamp(long lastJoinedTimestamp) {
        this.lastJoinedTimestamp = lastJoinedTimestamp;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
}
