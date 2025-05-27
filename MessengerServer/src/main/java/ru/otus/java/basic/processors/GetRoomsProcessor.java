package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.RoomProvider;

import java.time.Instant;
import java.util.StringJoiner;

public class GetRoomsProcessor implements MessageProcessor {
    RoomProvider roomProvider;

    public GetRoomsProcessor() {
        this.roomProvider = RoomProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        StringJoiner stringJoiner = new StringJoiner(", ");
        for(String roomName : roomProvider.getActiveRooms()){
            stringJoiner.add(roomName);
        }
        responseMsg.setText("Список комнат - "+stringJoiner.toString());
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
