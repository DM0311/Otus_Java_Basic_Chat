package ru.otus.java.basic.model.room;

import lombok.Getter;
import ru.otus.java.basic.model.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    @Getter
    private String roomName;
    @Getter
    private String roomPassword;
    @Getter
    private String owner;
    private Map<String, User> members;

    public Room(String roomName, String roomPassword, String owner) {
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.owner = owner;
        this.members = new HashMap<>();
    }

    public boolean enterRoom(User newUser, String password) {
        if (!password.equals(roomPassword)) {
            return false;
        }
        members.put(newUser.getUsername(), newUser);
        return true;
    }

    public boolean exitRoom(User user){
        if(!members.containsKey(user.getUsername())){
            return false;
        }
        members.remove(user.getUsername());
        return true;
    }

    public String getOwner(){
        return owner;
    }

    public List<String> getMembers(){
        return members.keySet().stream().toList();
    }
}
