package ru.otus.java.basic.processors;

import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.utils.MessageSerializer;

public class ExitProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;

    public ExitProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setCommand(Commands.EXIT);
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
        clientHandler.setWorking(false);
        if (clientHandler.getUser().getUsername() != null) {
            activeUsersProvider.removeClient(clientHandler.getUser().getUsername());
        }
    }
}
