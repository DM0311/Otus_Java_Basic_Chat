package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;

public class ExitProcessor implements MessageProcessor{
    ActiveUsersProvider activeUsersProvider;

    public ExitProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setCommand(Commands.EXIT);
        clientHandler.sendMsg(gson.toJson(responseMsg));
        clientHandler.setWorking(false);
        if(clientHandler.getUser().getUsername()!=null){
            activeUsersProvider.removeClient(clientHandler.getUser().getUsername());
        }
    }
}
