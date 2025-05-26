package ru.otus.java.basic.processors.messages;

import ru.otus.java.basic.Client;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.processors.messages.MessageProcessor;
import ru.otus.java.basic.utils.Printer;

public class ExitProcessor implements MessageProcessor {


    @Override
    public void process(Message message, Client client) {
        client.setActive(false);
    }
}
