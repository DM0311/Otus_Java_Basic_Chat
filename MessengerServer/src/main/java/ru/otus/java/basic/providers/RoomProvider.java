package ru.otus.java.basic.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.model.room.Room;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

    public List<String> checkRoomsActivity(){
        List<String> inactiveRoomNames = new ArrayList<>();
        Instant now = Instant.now();
        for(Room r : activeRooms.values()){
            Instant lastActivity = Instant.ofEpochSecond(r.getLastActivity());
            if(now.minus(7, ChronoUnit.DAYS).isAfter(lastActivity)){
                inactiveRoomNames.add(r.getRoomName());
            }
        }
        return inactiveRoomNames;
    }

    public int getOwnedRooms(String ownerName){
        int counter =0;
        for(Room r : activeRooms.values()){
            if(r.getOwner().equals(ownerName)){
                counter++;
            }
        }
        return counter;
    }
}
