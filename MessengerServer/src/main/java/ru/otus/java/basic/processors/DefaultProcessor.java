package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;

import java.time.Instant;

public class DefaultProcessor implements MessageProcessor {
    @Override
    public void process(Message message, ClientHandler clientHandler) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        Instant instant = Instant.now();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setTimeStamp(instant.getEpochSecond());
        responseMsg.setText("Некорректный формат сообщения - попробуйте снова");
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
