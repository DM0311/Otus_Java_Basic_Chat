package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.RoomProvider;

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

        clientHandler.getUser().setLogin(message.getParameters().get(0));
        clientHandler.getUser().setPassword(message.getParameters().get(1));
        boolean isAuthenticated = dataBaseProvider.authenticateUser(clientHandler.getUser());
        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        if(isAuthenticated){
            Room defaultRoom = roomProvider.getRoom("Default");
            String roomName = defaultRoom.enterRoom(clientHandler.getUser(),"");
            responseMsg.setText("Успешная авторизация. Вы попали в общий чат c именем "
                    +clientHandler.getUser().getUsername()
                    +".");
            responseMsg.setToUserName(clientHandler.getUser().getUsername());
            responseMsg.setRoomName(roomName);
            clientHandler.setAuthenticated(true);
            activeUsersProvider.addClient(clientHandler.getUser().getUsername(),clientHandler);
        }else {
            responseMsg.setText("Некорректный логин или пароль. Попробуйте еще раз");
        }
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }

}

