package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;

import java.time.Instant;

public class ChatMessageProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public ChatMessageProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        for(ClientHandler handler : activeUsersProvider.getAllClients()){
            handler.sendMsg(gson.toJson(message));
        }


    }

}

