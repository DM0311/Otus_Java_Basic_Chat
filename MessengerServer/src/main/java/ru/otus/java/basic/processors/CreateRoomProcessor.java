package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;

import java.time.Instant;

public class CreateRoomProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;
    RoomProvider roomProvider;

    public CreateRoomProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.roomProvider = RoomProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");

        String roomName = message.getParameters().get(0);
        String roomPassword = message.getParameters().size() > 1 ? message.getParameters().get(1) : " ";
        if(roomProvider.hasRoom(roomName)){
            responseMsg.setText("Невозможно создать комнату - такое имя уже занято");
        }else{
            Room instance = new Room(roomName, roomPassword, clientHandler.getUser().getUsername());
            roomProvider.addRoom(instance);
            if (!dataBaseProvider.addRoom(instance)) {
                responseMsg.setText("Невозможно создать комнату - попробуйте еще раз");
            } else {
                responseMsg.setText(instance.getRoomName() + " комната успешно создана - теперь вы можете продолжить общение в ней");
            }
        }
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
