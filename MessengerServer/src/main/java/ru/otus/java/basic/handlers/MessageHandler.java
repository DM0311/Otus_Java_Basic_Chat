package ru.otus.java.basic.handlers;

import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.processors.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private Map<Commands, MessageProcessor> messageProcessors;

    public MessageHandler() {
        this.messageProcessors = new ConcurrentHashMap<>();
        messageProcessors.put(Commands.AUTH, new AuthProcessor());
        messageProcessors.put(Commands.REGISTER, new RegisterProcessor());
        messageProcessors.put(Commands.EXIT, new ExitProcessor());
        messageProcessors.put(Commands.UNSUPPORTED, new DefaultProcessor());
        messageProcessors.put(Commands.DEFAULT, new DefaultProcessor());
        messageProcessors.put(Commands.CHAT_MESSAGE, new ChatMessageProcessor());
        messageProcessors.put(Commands.SHUT_DOWN, new ShutDownProcessor());
        messageProcessors.put(Commands.ACTIVE_USERS, new GetActiveUsersProcessor());
    }

    public void processMessage(Message message, ClientHandler clientHandler) {
        messageProcessors.get(message.getCommand()).process(message, clientHandler);
    }
}
