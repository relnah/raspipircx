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
    

}
