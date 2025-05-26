package ru.otus.java.basic.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;

import java.time.Instant;

public class RegisterProcessor implements MessageProcessor {
    DataBaseProvider dataBaseProvider;
    ActiveUsersProvider activeUsersProvider;

    public RegisterProcessor() {
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
    }

    @Override
    public void process(Message message, ClientHandler clientHandler) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Message responseMsg = new Message();
        responseMsg.setFromUserName("SERVER");

        String password = message.getParameters().get(1);

        if (password.length() < 4) {
            responseMsg.setText("Пароль не должен быть короче 4 символов. Придумайте новый пароль.");
        } else {
            clientHandler.getUser().setLogin(message.getParameters().get(0));
            clientHandler.getUser().setPassword(message.getParameters().get(1));
            clientHandler.getUser().setUsername(message.getParameters().get(2));
            boolean isRegistered = dataBaseProvider.registerUser(clientHandler.getUser());

            if (isRegistered) {
                responseMsg.setText("Успешная регистрация. Вы авторизованы и попали в общий чат.");
                responseMsg.setToUserName(clientHandler.getUser().getUsername());
                clientHandler.setAuthenticated(true);
                activeUsersProvider.addClient(clientHandler.getUser().getUsername(),clientHandler);
            } else {
                if (clientHandler.getUser().getUsername() == null) {
                    responseMsg.setText("Такое имя пользователя уже занято. Придумайте длругое имя.");
                } else {
                    responseMsg.setText("Такой логин уже занят. Придумайте другой логин");
                }
            }
        }
        Instant instant = Instant.now();
        responseMsg.setTimeStamp(instant.getEpochSecond());
        clientHandler.sendMsg(gson.toJson(responseMsg));
    }
}
