package ru.otus.java.basic.processors;

import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.model.user.Role;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class BanProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;
    ActiveUsersProvider activeUsersProvider;

    public BanProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        String bannedUsername = message.getParameters().get(0);
        int banPeriod = message.getParameters().size() > 1 ? Integer.parseInt(message.getParameters().get(1)) : 0;

        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());

        if (!clientHandler.getUser().getRole().equals(Role.ADMIN)) {
            responseMsg.setText("У вас недостаточно прав для исключения пользователя.");
            clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
            return;
        }

        if (activeUsersProvider.hasClient(bannedUsername)) {
            responseMsg.setCommand(Commands.EXIT);
            dataBaseProvider.setLastActivity(clientHandler.getUser());
            ClientHandler bannedClient = activeUsersProvider.getClient(bannedUsername);
            responseMsg.setText("Вы были забанены администратором");
            bannedClient.sendMsg(MessageSerializer.serialize(responseMsg));
            bannedClient.setWorking(false);
            activeUsersProvider.removeClient(bannedClient.getUser().getUsername());
            dataBaseProvider.banUser(bannedUsername, banPeriod);
        }


    }

}

