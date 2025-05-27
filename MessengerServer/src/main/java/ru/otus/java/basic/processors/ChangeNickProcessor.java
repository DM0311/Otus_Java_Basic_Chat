package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;

import java.time.Instant;

public class ChangeNickProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;
    ActiveUsersProvider activeUsersProvider;

    public ChangeNickProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());

        String prevNick = clientHandler.getUser().getUsername();
        String newNick = message.getParameters().get(0);

        if (dataBaseProvider.nickIsUsed(newNick)) {
            responseMsg.setText("Указанное имя пользователя уже занято - придумайте другое.");
        }else{
            clientHandler.getUser().setUsername(newNick);
            activeUsersProvider.removeClient(prevNick);
            activeUsersProvider.addClient(newNick,clientHandler);
            if(dataBaseProvider.changeNick(prevNick, newNick)){
                responseMsg.setText("Имя пользовательно успешно изменено на "+clientHandler.getUser().getUsername());
            }else {
                responseMsg.setText("Не получилось изменить имя пользователя. Попробуйте еще раз.");
            }
        }
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
