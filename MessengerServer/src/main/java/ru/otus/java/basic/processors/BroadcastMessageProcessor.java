package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.utils.MessageSerializer;

public class BroadcastMessageProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public BroadcastMessageProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        for(ClientHandler handler : activeUsersProvider.getAllClients()){
            handler.sendMsg(MessageSerializer.serialize(message));
        }


    }

}

