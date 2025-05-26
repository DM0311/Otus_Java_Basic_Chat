package ru.otus.java.basic.handlres;

import ru.otus.java.basic.Client;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.processors.messages.DefaultProcessor;
import ru.otus.java.basic.processors.messages.ExitProcessor;
import ru.otus.java.basic.processors.messages.MessageProcessor;
import ru.otus.java.basic.processors.messages.ShutDownProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private Map<Commands, MessageProcessor> messageProcessors;

    public MessageHandler() {
        this.messageProcessors = new ConcurrentHashMap<>();
        messageProcessors.put(Commands.DEFAULT, new DefaultProcessor());
        messageProcessors.put(Commands.EXIT, new ExitProcessor());
        messageProcessors.put(Commands.SHUT_DOWN, new ShutDownProcessor());
    }

    public void processMessage(Message message, Client client) {

        if (message.getCommand() == null) {
            messageProcessors.get(Commands.DEFAULT).process(message, client);
        } else {
            if (messageProcessors.containsKey(message.getCommand())) {
                messageProcessors.get(message.getCommand()).process(message, client);
            } else {
                messageProcessors.get(Commands.DEFAULT).process(message, client);
            }
        }


    }
}
