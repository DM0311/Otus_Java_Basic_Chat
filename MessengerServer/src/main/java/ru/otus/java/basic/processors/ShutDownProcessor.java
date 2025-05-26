package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.user.Role;
import ru.otus.java.basic.providers.ActiveUsersProvider;

import java.time.Instant;

public class ShutDownProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public ShutDownProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        if (clientHandler.getUser().getRole().equals(Role.ADMIN)) {
            responseMsg.setText("Работа сервера завершена. Вы были отключены.");
            responseMsg.setCommand(Commands.SHUT_DOWN);
            for(ClientHandler client : activeUsersProvider.getAllClients()){
                client.sendMsg(gson.toJson(responseMsg));
                client.setWorking(false);
                client.disconnect();
            }
            activeUsersProvider.removeAllClients();
            clientHandler.getServer().shutDown();
        }else {
            responseMsg.setText("У вас недостаточно прав для отключения сервера.");
            clientHandler.sendMsg(gson.toJson(responseMsg));
        }

    }

}

