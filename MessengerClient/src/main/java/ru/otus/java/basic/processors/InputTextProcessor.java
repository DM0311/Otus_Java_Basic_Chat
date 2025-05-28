package ru.otus.java.basic.processors;

import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.model.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class InputTextProcessor {

    public static Message processInput(String inputText, boolean isAuthenticated) {

        String[] parts = inputText.split(" ");

        Message msg = new Message();
        Instant instant = Instant.now();
        msg.setTimeStamp(instant.getEpochSecond());

        int partCounter = 0;
        StringBuilder messageText = new StringBuilder();

        for (String element : parts) {
            if (partCounter == 0) {

                if (element.startsWith("/")) {
                    msg.setCommand(Commands.enumForValue(element));
                } else {
                    if (isAuthenticated) {
                        msg.setCommand(Commands.CHAT_MESSAGE);
                    } else {
                        msg.setCommand(Commands.UNSUPPORTED);
                    }
                    messageText.append(element+" ");
                }

            } else {
                if (partCounter <= msg.getCommand().getNumberOfParams()) {
                    msg.getParameters().add(element);
                } else {
                    messageText.append(element + " ");
                }
            }
            partCounter++;
        }
        switch (msg.getCommand()){
            case CREATE_ROOM, ENTER_ROOM, BAN -> {if (msg.getCommand().getNumberOfParams() <1) {
                msg.setCommand(Commands.UNSUPPORTED);
                return msg;
            }}
            default -> {if (msg.getCommand().getNumberOfParams() != msg.getParameters().size()) {
                msg.setCommand(Commands.UNSUPPORTED);
                return msg;
            }}
        }

        msg.setText(messageText.toString());
        return msg;
    }
}
