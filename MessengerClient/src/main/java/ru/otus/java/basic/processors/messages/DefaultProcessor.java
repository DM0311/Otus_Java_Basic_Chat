package ru.otus.java.basic.processors.messages;

import ru.otus.java.basic.Client;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.processors.messages.MessageProcessor;
import ru.otus.java.basic.utils.Printer;

public class DefaultProcessor implements MessageProcessor {


    @Override
    public void process(Message message, Client client) {
        if(message.getFromUserName().equals(client.getUserName())){
            return;
        }
        Printer.printMessage(message);
    }
}
