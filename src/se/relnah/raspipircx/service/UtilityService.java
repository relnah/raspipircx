/**
 * 
 */
package se.relnah.raspipircx.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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

    /**
     * Gets resource bundle with application texts.
     * @param baseName
     * @param locale
     * @return ResourceBundle
     */
    public static ResourceBundle getTextBundle(String baseName, Locale locale) {
        ClassLoader loader = null;
        try {
        File file = new File("./texts");
        URL[] urls = {file.toURI().toURL()};
        loader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ResourceBundle textBundle = ResourceBundle.getBundle(baseName, locale, loader);
        return textBundle;
    }
    
    

}
