package ru.otus.java.basic.processors;

import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class ExitProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;
    DataBaseProvider dataBaseProvider;

    public ExitProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.dataBaseProvider = DataBaseProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setCommand(Commands.EXIT);
        responseMsg.setTimeStamp(Instant.now().getEpochSecond());
        responseMsg.setText("Вы вышли из чата.");
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
        clientHandler.setWorking(false);
        if (clientHandler.getUser().getUsername() != null) {
            dataBaseProvider.setLastActivity(clientHandler.getUser());
            activeUsersProvider.removeClient(clientHandler.getUser().getUsername());
        }
    }
}
