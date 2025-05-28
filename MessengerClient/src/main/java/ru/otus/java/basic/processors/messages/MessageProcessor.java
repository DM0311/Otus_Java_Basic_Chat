package ru.otus.java.basic.processors.messages;

import ru.otus.java.basic.Client;
import ru.otus.java.basic.model.Message;

public interface MessageProcessor {
    public void process(Message message, Client client);
}
