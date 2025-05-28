package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class GetActiveUsersProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public GetActiveUsersProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подключенные пользователи: ");
        for (String userName : activeUsersProvider.getActiveUsers()) {
            stringBuilder.append(userName + " ");
        }
        responseMsg.setText(stringBuilder.toString());
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
    }

}

