/**
 * 
 */
package se.relnah.raspipircx.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import se.relnah.raspipircx.pojo.BotUser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * @author davbj
 *
 */
public final class SerializeService {

    protected SerializeService(){};
    
    
    public static void saveGsonUserList(List<BotUser> userList) {
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        List<String> lines = new ArrayList<String>();
        lines.add(json);
        
        System.out.println("Saving user list...");
        
        Path path = Paths.get("./users.json");

        try {
            Files.write(path, lines, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<BotUser> loadGsonUserList() {
        Gson gson = new Gson();
        List<String> lines = new ArrayList<String>();

        Path path = Paths.get("./users.json");
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String json = "";
        
        for (String line : lines) {
            json += line;
        }
        
        Type listType = new TypeToken<List<BotUser>>(){}.getType();

        
        List<BotUser> userList = gson.fromJson(json, listType);
        
        return userList;
        
    }    
    
}
