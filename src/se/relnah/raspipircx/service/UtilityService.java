/**
 * 
 */
package se.relnah.raspipircx.service;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.relnah.raspipircx.pojo.BotUser;

/**
 * @author davbj
 *
 */
public final class UtilityService {

    protected UtilityService() {}
    private static Logger LOG = LoggerFactory.getLogger(UtilityService.class);

    /**
     * Gets user matching nick in userList. Removes trailing _ and everything after and including |
     * to better handle temp nicks.
     * @param nick
     * @param userList
     * @return BotUser or null if user wasn't found.
     */
    public static BotUser getUser(String nick, List<BotUser> userList) {

        BotUser usr = null;
        
        nick = cleanNick(nick);
        
        for (BotUser user : userList) {
            if(nick.equalsIgnoreCase(user.getNick())) {
                usr = user;
                break;
            }
        }
        
        return usr;
        
    }

    /**
     * Attempts to clean alternative nick markers such as nick|2 and nick_
     * @param nick
     * @return
     */
    public static String cleanNick(String nick) {
        //Clear tempnick
        if (nick.endsWith("_")) {
            nick = nick.substring(0, nick.lastIndexOf('_'));
        }

        if (nick.contains("|")) {
            nick = nick.substring(0, nick.indexOf('|'));
        }
        return nick;
    }

    /**
     * Gets resource bundle with application texts.
     * @param baseName
     * @param string 
     * @param locale
     * @return ResourceBundle
     */
    public static ResourceBundle getTextBundle(String rootPath, String baseName, Locale locale) {
        ClassLoader loader = null;
        try {
        File file = new File(rootPath + "/texts");
        URL[] urls = {file.toURI().toURL()};
        loader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            LOG.error(UtilityService.stackTraceToString(e));
        }
        
        ResourceBundle textBundle = ResourceBundle.getBundle(baseName, locale, loader);
        return textBundle;
    }
    
    /**
     * Takes bundle key, data arguments and fills placeholder with data.
     * @param textBundle 
     * @param bundleKey
     * @param arguments
     * @return String
     */
    public static String getText(ResourceBundle textBundle, String bundleKey, String[] arguments) {
        MessageFormat formatter = new MessageFormat("");
        
        formatter.setLocale(textBundle.getLocale());

        formatter.applyPattern(textBundle.getString(bundleKey));
        
        return formatter.format(arguments);
        
    }

    /**
     * Reads resourcebundle and extracts available commands.
     * @param textBundle
     * @return
     */
    public static Map<String, List<Map<String, String>>> getCommands(
            ResourceBundle textBundle) {

        Map<String, List<Map<String, String>>> commands = new HashMap<String, List<Map<String, String>>>();
        List<Map<String, String>> publicCommands = new ArrayList<Map<String, String>>();
        List<Map<String, String>> adminCommands = new ArrayList<Map<String, String>>();

        Enumeration<String> enumKeys = textBundle.getKeys();
        
        while (enumKeys.hasMoreElements()) {
            String key = enumKeys.nextElement();
            
            //public commands
            if (key.startsWith("command.") && !key.startsWith("command.admin.")) {
                String command = key.substring(key.indexOf('.') + 1, key.length());
                if (!command.contains(".")) {
                    Map<String, String> commandMap = new HashMap<String, String>();
                    commandMap.put("key", command);
                    commandMap.put("command", textBundle.getString("command." + command));
                    publicCommands.add(commandMap);
                }
            } else if (key.startsWith("command.admin.")) { //admin commands
                String command = key.substring(key.indexOf('.', key.indexOf('.') + 1) + 1, key.length());
                if (!command.contains(".")) {
                    Map<String, String> commandMap = new HashMap<String, String>();
                    commandMap.put("key", command);
                    commandMap.put("command", textBundle.getString("command.admin." + command));
                    adminCommands.add(commandMap);
                }
            }
            
         }
        
        commands.put("public", publicCommands);
        commands.put("admin", adminCommands);
        
        return commands;
    }

    /**
     * Checks if users nick matches admin and if user is verified
     * @param user
     * @return boolean
     */
    public static boolean isAdmin(User user, String message) {
        
        if (user.getNick().equals("David_B") && message.startsWith(".")) {
            
            //Do separate check on verified. Minimize impact of expensive check.
            if (user.isVerified()) {
                return true;
            }
           
        }
        return false;
    }

    /**
     * Returns first channel in the list of channels the bot has joined
     * @param event
     * @return
     */
    public static Channel getBotChan(Event<PircBotX> event) {
        return event.getBot().getUserBot().getChannels().first();
    }

    
    /**
     * Get stacktrace as a string
     * @param e
     */
    public static String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw); 
        return sw.toString();        
    }
    
    /**
     * Removes invalid characters from nick
     * @param rawNick
     * @return clean nick
     */
    public static String removeInvalidCharacters(String rawNick) {
        return rawNick.replaceAll("[.:,;]","");
    }
    


}
