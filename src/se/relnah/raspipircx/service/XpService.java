/**
 * 
 */
package se.relnah.raspipircx.service;

/**
 * @author davbj
 *
 */
public final class XpService {

    /**
     * 
     */
    protected XpService() {}
    
    /**
     * Calculates level based on current XP and current level.
     * @param newXp
     * @param lvl
     * @return int calculated level
     */
    public static int calculateLevel(int newXp, int lvl) {

        int nextLvlReq = (4 * lvl) * ((3 * lvl) + 45);
        
        while (newXp > nextLvlReq) {
            lvl++;
            nextLvlReq = (4 * lvl) * ((3 * lvl) + 45);
        }
        
        return lvl;
    }
    

}
