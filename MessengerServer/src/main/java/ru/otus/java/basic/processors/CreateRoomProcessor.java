package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

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

        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        Instant instant = Instant.now();
        String roomName = message.getParameters().get(0);
        String roomPassword = message.getParameters().size() > 1 ? message.getParameters().get(1) : " ";
        if(roomProvider.hasRoom(roomName)){
            responseMsg.setText("Невозможно создать комнату - такое имя уже занято");
        }else{
            Room r = new Room(roomName, roomPassword, clientHandler.getUser().getUsername());
            r.setLastActivity(instant.getEpochSecond());
            roomProvider.addRoom(r);
            if (!dataBaseProvider.addRoom(r)) {
                responseMsg.setText("Невозможно создать комнату - попробуйте еще раз");
            } else {
                responseMsg.setText(r.getRoomName() + " комната успешно создана - теперь вы можете продолжить общение в ней");
            }
        }
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
    }
}
