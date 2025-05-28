package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;

public class ChatMessageProcessor implements MessageProcessor {
    ActiveUsersProvider activeUsersProvider;
    RoomProvider roomProvider;
    DataBaseProvider dataBaseProvider;

    public ChatMessageProcessor() {
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.roomProvider = RoomProvider.getInstance();
        this.dataBaseProvider = DataBaseProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        dataBaseProvider.archiveMessage(message);
        Instant instant = Instant.now();
        roomProvider.getRoom(message.getRoomName()).setLastActivity(instant.getEpochSecond());
        for (String userName : roomProvider.getRoom(message.getRoomName()).getMembers()) {
            if (activeUsersProvider.hasClient(userName)) {
                activeUsersProvider.getClient(userName).sendMsg(MessageSerializer.serialize(message));
            }

        }


    }

}

