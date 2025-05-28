package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.model.user.User;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;
import ru.otus.java.basic.utils.MessageSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LastActivityProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;

    public LastActivityProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");
        responseMsg.setTimeStamp(Instant.now().getEpochSecond());

        User usr = new User();
        usr.setUsername(message.getParameters().get(0));
        Long ts = dataBaseProvider.getLastActivity(usr);
        if (ts == 0L) {
            responseMsg.setText(usr.getUsername()+" пользователь с таким именем не найден");
        }else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
            LocalDateTime lastActivityTS = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(ts),
                    ZoneId.systemDefault());
            String timestamp = dateTimeFormatter.format(lastActivityTS);
            responseMsg.setText("Последнее время активности пользователя - "+timestamp);
        }
        clientHandler.sendMsg(MessageSerializer.serialize(responseMsg));
    }

}

