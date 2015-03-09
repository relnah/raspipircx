/**
 * 
 */
package se.relnah.raspipircx.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.relnah.raspipircx.pojo.BotUser;
import se.relnah.raspipircx.pojo.UserTitle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * @author davbj
 *
 */
public final class SerializeService {

    protected SerializeService(){};
    private static Logger LOG = LoggerFactory.getLogger(SerializeService.class);
    
    /**
     * Saves userlist in json format to file
     * @param rootPath 
     * @param userList
     */
    public static void saveGsonUserList(String rootPath, List<BotUser> userList) {
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        List<String> lines = new ArrayList<String>();
        lines.add(json);
        
        LOG.info("Saving user list...");
        
        Path path = Paths.get(rootPath + "/users.json");

        try {
            Files.write(path, lines, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            LOG.error(UtilityService.stackTraceToString(e));
        }
    }
    
    /**
     * Loads user list from file in json format
     * @param rootPath 
     * @return
     */
    public static List<BotUser> loadGsonUserList(String rootPath) {
        Gson gson = new Gson();
        List<String> lines = new ArrayList<String>();

        Path path = Paths.get(rootPath + "/users.json");
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(UtilityService.stackTraceToString(e));
        }
        
        String json = "";
        
        for (String line : lines) {
            json += line;
        }
        
        Type listType = new TypeToken<List<BotUser>>(){}.getType();

        
        List<BotUser> userList = gson.fromJson(json, listType);
        
        return userList;
        
    }    
    
    /**
     * Saves title list in json format to file
     * @param userList
     */
    public static void saveGsonTitleList(List<UserTitle> titleList) {
        Gson gson = new Gson();
        String json = gson.toJson(titleList);
        List<String> lines = new ArrayList<String>();
        lines.add(json);
        
        LOG.info("Saving title list...");

        
        Path path = Paths.get("./titles.json");

        try {
            Files.write(path, lines, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            LOG.error(UtilityService.stackTraceToString(e));
        }
    }
    
    /**
     * Loads title list from file in json format
     * @return
     */
    public static List<UserTitle> loadGsonTitleList() {
        Gson gson = new Gson();
        List<String> lines = new ArrayList<String>();

        Path path = Paths.get("./titles.json");
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(UtilityService.stackTraceToString(e));
        }
        
        String json = "";
        
        for (String line : lines) {
            json += line;
        }
        
        Type listType = new TypeToken<List<UserTitle>>(){}.getType();

        
        List<UserTitle> titleList = gson.fromJson(json, listType);
        
        if (titleList == null) {
            return new ArrayList<UserTitle>();
        }
        return titleList;
        
    }    
    
}
