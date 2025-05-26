package ru.otus.java.basic.processors;

import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.Message;

public interface MessageProcessor {
    public void process(Message message, ClientHandler clientHandler);
}
