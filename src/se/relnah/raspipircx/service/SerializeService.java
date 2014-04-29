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
import java.util.ArrayList;
import java.util.List;

import se.relnah.raspipircx.pojo.BotUser;


/**
 * @author davbj
 *
 */
public final class SerializeService {

    protected SerializeService(){};
    
    

    public static void saveUserList(List<BotUser> userList) {
        // Serialize userList
        try {
            System.out.println("Saving user list...");
            FileOutputStream fileOut = new FileOutputStream("./users.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(userList);
            out.close();
            fileOut.close();
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static List<BotUser> loadUserList() {
        List<BotUser> userList = new ArrayList<BotUser>();
        // Deserialize userList
        try {
            FileInputStream fileIn = new FileInputStream("./users.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            userList = (List<BotUser>) in.readObject();
            in.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return userList;
        
        
    }

    
}
