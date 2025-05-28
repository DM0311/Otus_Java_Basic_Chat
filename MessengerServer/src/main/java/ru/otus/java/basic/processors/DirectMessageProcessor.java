package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class DirectMessageProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public DirectMessageProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        ClientHandler toClient = activeUsersProvider.getClient(message.getParameters().get(0));
        Instant instant = Instant.now();
        Message responseMsg = new Message();
        responseMsg.setFromUserName(message.getFromUserName());
        responseMsg.setTimeStamp(instant.getEpochSecond());
        responseMsg.setText(message.getText());
        toClient.sendMsg(MessageSerializer.serialize(responseMsg));
    }
}
