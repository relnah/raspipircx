/**
 * 
 */
package se.relnah.raspipircx.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<UserTitle> titles;
    private int choosenTitleIndex;
    private int numTypedLines;
    private int numKudosGiven;
    private int numKudosRecieved;
    private Map<String, String> settings;
    
    
    
    /**
     * @param nick
     */
    public BotUser(String nick) {
        this.nick = nick;
        this.xp = 0;
        this.level = 0;
        this.consecutiveDays = 1;
        this.titles = new ArrayList<UserTitle>();
        titles.add(0, new UserTitle("", 0));
        this.choosenTitleIndex = 0;
        this.numTypedLines = 0;
        this.numKudosGiven = 0;
        this.numKudosRecieved = 0;
        this.settings = new HashMap<String, String>();
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
     * Increas the number of kudos user has given to other users by 1.
     * Returns the new sum.
     * @return
     */
    public int increasKudosGiven() {
        this.numKudosGiven++;
        return numKudosGiven;
    }
    
    /**
     * Increas number of kudos user has recieved from other users by 1.
     * Returns the new sum.
     * @return
     */
    public int increasKudosRecieved() {
        this.numKudosRecieved++;
        return numKudosRecieved;
    }
    
    /**
     * Sets a setting on the user
     * @param key
     * @param value
     */
    public void setSetting(String key, String value) {
        settings.put(key, value);
    }
    
    /**
     * Gets value of a setting
     * @param key
     * @return
     */
    public String getSetting(String key) {
        return settings.get(key);
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
    public ArrayList<UserTitle> getTitles() {
        return titles;
    }

    /**
     * @param titles the titles to set
     */
    public void setTitles(ArrayList<UserTitle> titles) {
        this.titles = titles;
    }
    
    /**
     * Adds a standard title to user
     * @param title
     * @param levelReq
     */
    public void addTitle(String title, int levelReq) {
        this.titles.add(new UserTitle(title, levelReq));
    }

    /**
     * Adds a standard title to user
     * @param usrTitle
     */
    public void addTitle(UserTitle usrTitle) {
        this.titles.add(usrTitle);
    }

    /**
     * Adds a custom title to user
     * @param title
     * @param levelReq
     * @param isCustom
     */
    public void addCustomTitle(String title, int levelReq) {
        this.titles.add(new UserTitle(title, levelReq, true));
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
    public UserTitle getSelectedTitle() {
        return titles.get(choosenTitleIndex);
    }

    /**
     * Increases number of lines typed by user by 1. Returns new sum.
     * @return
     */
    public int increasLinesTyped() {
        this.numTypedLines++;
        return numTypedLines;
    }

    /**
     * @return the numTypedLines
     */
    public int getNumTypedLines() {
        return numTypedLines;
    }

    /**
     * @return the numKudosGiven
     */
    public int getNumKudosGiven() {
        return numKudosGiven;
    }

    /**
     * @return the numKudosRecieved
     */
    public int getNumKudosRecieved() {
        return numKudosRecieved;
    }
    
}
