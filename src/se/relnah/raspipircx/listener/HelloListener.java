package se.relnah.raspipircx.listener;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class HelloListener extends ListenerAdapter<PircBotX> {
    
    private List<String> userList = new ArrayList<String>();
    
    public void onMessage(MessageEvent<PircBotX> event) {

        if (event.getMessage().equals(".hello")) {
            String evtUsername = event.getUser().getNick();
            Boolean knownUser = false;
            
            for (String username : userList) {
                if (evtUsername.equalsIgnoreCase(username)) {
                    knownUser = true;
                    break;
                }
            }
            
            if (knownUser) {
                event.respond("Hello! I remember you!");
            } else {
                userList.add(evtUsername);
                event.respond("Hello! Nice to meet you!");
            }
            
            
        }
    }
}