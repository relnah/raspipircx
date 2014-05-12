/**
 * 
 */
package se.relnah.raspipircx.pojo;

import java.io.Serializable;
import java.util.ArrayList;

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
    private int consecutiveDays;
    private ArrayList<String> titles;
    private int choosenTitleIndex;
    private int numTypedLines;
    private int numKudosGiven;
    private int numKudosRecieved;
    
    
    
    /**
     * @param nick
     */
    public BotUser(String nick) {
        this.nick = nick;
        this.xp = 0;
        this.level = 0;
        this.consecutiveDays = 1;
        this.titles = new ArrayList<String>();
        titles.add(0, "");
        this.choosenTitleIndex = 0;
        this.numTypedLines = 0;
        this.numKudosGiven = 0;
        this.numKudosRecieved = 0;
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
     * Increases consecutive days by one and returns the sum.
     * @return
     */
    public int increaseConsecutiveDays() {
        this.consecutiveDays++;
        return this.consecutiveDays;
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

    /**
     * @return the consecutiveDays
     */
    public int getConsecutiveDays() {
        return consecutiveDays;
    }

    /**
     * @param consecutiveDays the consecutiveDays to set
     */
    public void setConsecutiveDays(int consecutiveDays) {
        this.consecutiveDays = consecutiveDays;
    }

    /**
     * @return the titles
     */
    public ArrayList<String> getTitles() {
        return titles;
    }

    /**
     * @param titles the titles to set
     */
    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }
    
    public void addTitle(String title) {
        this.titles.add(title);
    }

    /**
     * @return the choosenTitleIndex
     */
    public int getChoosenTitleIndex() {
        return choosenTitleIndex;
    }

    /**
     * @param choosenTitleIndex the choosenTitleIndex to set
     */
    public void setChoosenTitleIndex(int choosenTitleIndex) {
        this.choosenTitleIndex = choosenTitleIndex;
    }

    /**
     * Get the title the user has selected as active
     * @return String title
     */
    public String getSelectedTitle() {
        return titles.get(choosenTitleIndex);
    }
    
}
