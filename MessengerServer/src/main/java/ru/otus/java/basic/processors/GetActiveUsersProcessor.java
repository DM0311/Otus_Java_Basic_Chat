package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;

import java.time.Instant;

public class GetActiveUsersProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public GetActiveUsersProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подключенные пользователи: ");
        for (String userName : activeUsersProvider.getActiveUsers()){
            stringBuilder.append(userName+" ");
        }
        responseMsg.setText(stringBuilder.toString());
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }

}

