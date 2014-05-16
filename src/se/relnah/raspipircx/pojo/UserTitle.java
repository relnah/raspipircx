/**
 * 
 */
package se.relnah.raspipircx.pojo;

import java.io.Serializable;

/**
 * @author davbj
 *
 */
public class UserTitle implements Serializable{

    private static final long serialVersionUID = 3839767018966811873L;
    
    private String title;
    private boolean custom;
    private int levelReq;
    
    
    /**
     * 
     */
    public UserTitle(String title, int levelReq) {
        this.title = title;
        this.levelReq = levelReq;
        this.custom = false;
    }

    public UserTitle(String title, int levelReq, boolean isCustom) {
        this.title = title;
        this.levelReq = levelReq;
        this.custom = isCustom;
    }


    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Check if this is a custom title
     * @return boolean
     */
    public boolean isCustom() {
        return custom;
    }

    /**
     * @return the levelReq
     */
    public int getLevelReq() {
        return levelReq;
    }

    /**
     * If a title contains text method will return it in quoutes
     * @return
     */
    public String getQuoutedTitle() {
        
        String returnTitle = "";
        
        if (!"".equals(title) || title == null) {
            returnTitle = "\"" + title + "\"";
        }
        
        return returnTitle;
    }



}
