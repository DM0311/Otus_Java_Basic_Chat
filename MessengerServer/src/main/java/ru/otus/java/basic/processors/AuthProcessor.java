package ru.otus.java.basic.processors;

import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class AuthProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;
    ActiveUsersProvider activeUsersProvider;
    RoomProvider roomProvider;

    public AuthProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.roomProvider = RoomProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        String defaultRoomName = "Default";

        clientHandler.getUser().setLogin(message.getParameters().get(0));
        clientHandler.getUser().setPassword(message.getParameters().get(1));
        boolean isAuthenticated = dataBaseProvider.authenticateUser(clientHandler.getUser());
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        if(isAuthenticated){
            Room defaultRoom = roomProvider.getRoom(defaultRoomName);
            defaultRoom.enterRoom(clientHandler.getUser()," ");
            responseMsg.setText("Успешная авторизация. Вы попали в общий чат c именем "
                    +clientHandler.getUser().getUsername()
                    +".");
            responseMsg.setToUserName(clientHandler.getUser().getUsername());
            responseMsg.setRoomName(defaultRoomName);
            clientHandler.setAuthenticated(true);
            activeUsersProvider.addClient(clientHandler.getUser().getUsername(),clientHandler);
        }else {
            responseMsg.setText("Некорректный логин или пароль. Попробуйте еще раз");
        }
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
    }

}

