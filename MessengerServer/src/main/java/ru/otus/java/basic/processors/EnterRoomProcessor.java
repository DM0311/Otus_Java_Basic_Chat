package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;
import java.util.List;

public class EnterRoomProcessor implements MessageProcessor {
    private RoomProvider roomProvider;
    private DataBaseProvider dataBaseProvider;

    public EnterRoomProcessor() {
        this.roomProvider = RoomProvider.getInstance();
        this.dataBaseProvider = DataBaseProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        String currentRoom = message.getRoomName();
        String newRoomName = message.getParameters().get(0);
        String roomPassword = message.getParameters().size() > 1 ? message.getParameters().get(1) : " ";
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());

        if (!roomProvider.hasRoom(newRoomName)) {
            responseMsg.setText("Комнаты стаким названием не существует.");
            clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
            return;
        } else {
            if (roomProvider.getRoom(newRoomName).enterRoom(clientHandler.getUser(), roomPassword)) {
                roomProvider.getRoom(newRoomName).setLastActivity(instant.getEpochSecond());
                responseMsg.setRoomName(newRoomName);
                responseMsg.setText("Вы вошли в команту - " + newRoomName);
                roomProvider.getRoom(currentRoom).exitRoom(clientHandler.getUser());
                clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
                List<Message> messages = dataBaseProvider.getLastMsgInRoom(newRoomName);
                for (Message msg : messages) {
                    clientHandler.sendMsg(MessageSerializer.serialize(msg));
                }
            } else {
                responseMsg.setText("Некорректный пароль для входа в комнату. Попробуйте еще раз");
                clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
            }
        }
    }
}
