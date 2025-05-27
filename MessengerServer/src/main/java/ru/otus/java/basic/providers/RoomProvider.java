package ru.otus.java.basic.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.model.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomProvider {
    private static volatile RoomProvider instance;
    private Map<String, Room> activeRooms;
    private final Logger log;

    private RoomProvider() {
        this.activeRooms = new ConcurrentHashMap<>();
        this.log = LogManager.getLogger(ActiveUsersProvider.class);
    }

    public static RoomProvider getInstance() {
        RoomProvider localInstance = instance;
        if (localInstance == null) {
            synchronized (RoomProvider.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RoomProvider();
                }
            }
        }
        return localInstance;
    }

    public void addRoom(Room newRoom){
        activeRooms.put(newRoom.getRoomName(),newRoom);
    }

    public Room getRoom(String roomName) {
        return activeRooms.get(roomName);
    }

    public List<String> deleteRoom(String roomName) {
        if (!activeRooms.containsKey(roomName)) {
            return Collections.emptyList();
        }
        List<String> usersToDefaultRoom = activeRooms.get(roomName).getMembers();
        activeRooms.remove(roomName);
        return usersToDefaultRoom;
    }

    public List<String> getActiveRooms(){
        return activeRooms.keySet().stream().toList();
    }

    public boolean hasRoom(String roomName){
        return activeRooms.containsKey(roomName);
    }
}
