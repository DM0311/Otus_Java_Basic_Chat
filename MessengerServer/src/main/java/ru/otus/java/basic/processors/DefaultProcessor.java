package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class DefaultProcessor implements MessageProcessor {
    @Override
    public void process(Message message, ClientHandler clientHandler) {
        Instant instant = Instant.now();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setTimeStamp(instant.getEpochSecond());
        responseMsg.setText("Некорректный формат сообщения - попробуйте снова");
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
    }
}
