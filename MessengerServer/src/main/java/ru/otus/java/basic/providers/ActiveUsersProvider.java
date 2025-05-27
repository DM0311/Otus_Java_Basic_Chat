package ru.otus.java.basic.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.handlers.ClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveUsersProvider {
    private static volatile ActiveUsersProvider instance;
    private Map<String, ClientHandler> activeUsers;
    private final Logger log;

    private ActiveUsersProvider() {
        this.activeUsers = new ConcurrentHashMap<>();
        this.log = LogManager.getLogger(ActiveUsersProvider.class);
    }

    public static ActiveUsersProvider getInstance() {
        ActiveUsersProvider localInstance = instance;
        if (localInstance == null) {
            synchronized (ActiveUsersProvider.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ActiveUsersProvider();
                }
            }
        }
        return localInstance;
    }

    public ClientHandler getClient(String userName){
        return activeUsers.get(userName);
    }

    public void addClient (String userName, ClientHandler clientHandler){
        activeUsers.put(userName, clientHandler);
    }

    public void removeClient(String userName){
        activeUsers.get(userName).disconnect();
        activeUsers.remove(userName);
    }

    public List<ClientHandler> getAllClients(){
        return activeUsers.values().stream().toList();
    }

    public void removeAllClients(){
        for(ClientHandler handler : activeUsers.values()){
            handler.disconnect();
        }
        activeUsers.clear();
    }

    public List<String> getActiveUsers(){
        return activeUsers.keySet().stream().toList();
    }

    public boolean hasClient(String userName){
        return activeUsers.containsKey(userName);
    }
}
