package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;

import java.time.Instant;

public class DirectMessageProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public DirectMessageProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }
    @Override
    public void process(Message message, ClientHandler clientHandler) {

        ClientHandler toClient = activeUsersProvider.getClient(message.getParameters().get(0));
        Gson gson = new GsonBuilder().serializeNulls().create();
        Instant instant = Instant.now();
        Message responseMsg = new Message();
        responseMsg.setFromUserName(message.getFromUserName());
        responseMsg.setTimeStamp(instant.getEpochSecond());
        responseMsg.setText(message.getText());
        toClient.sendMsg(gson.toJson(responseMsg));
    }
}
