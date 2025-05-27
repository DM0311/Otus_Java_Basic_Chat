package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;

import java.time.Instant;

public class EnterRoomProcessor implements MessageProcessor {
    RoomProvider roomProvider;

    public EnterRoomProcessor() {
        this.roomProvider = RoomProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        String currentRoom = message.getRoomName();
        String newRoomName = message.getParameters().get(0);
        String roomPassword = message.getParameters().size() > 1 ? message.getParameters().get(1) : " ";
        if (!roomProvider.hasRoom(newRoomName)) {
            responseMsg.setText("Комнаты стаким названием не существует.");
        } else {
            if (roomProvider.getRoom(newRoomName).enterRoom(clientHandler.getUser(), roomPassword)) {
                responseMsg.setRoomName(newRoomName);
                responseMsg.setText("Вы вошли в команту - "+newRoomName);
                roomProvider.getRoom(currentRoom).exitRoom(clientHandler.getUser());
            } else {
                responseMsg.setText("Некорректный пароль для входа в комнату. Попробуйте еще раз");
            }
        }
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
