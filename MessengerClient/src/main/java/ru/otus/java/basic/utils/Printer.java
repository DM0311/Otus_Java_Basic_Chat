package ru.otus.java.basic.utils;

import ru.otus.java.basic.model.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Printer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

    public static void printMessage(Message message) {

        LocalDateTime messageTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(message.getTimeStamp()),
                ZoneId.systemDefault());
        String timestamp = dateTimeFormatter.format(messageTime);
        String messageFrom = message.getFromUserName();
        System.out.printf("%-25s %-10s: %s\n", timestamp, messageFrom, message.getText());
    }
}
