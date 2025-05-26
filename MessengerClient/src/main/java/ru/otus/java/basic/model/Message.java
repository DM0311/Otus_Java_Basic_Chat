package ru.otus.java.basic.model;

import com.google.gson.Gson;
import lombok.Data;
import ru.otus.java.basic.commands.Commands;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {
    private Long timeStamp;
    private String fromUserName;
    private Long chatId;
    private String toUserName;
    private Commands command;
    private List<String> parameters = new ArrayList<>();
    private String text;
    private static Gson gson = new Gson();

    public static Message parseMessage(String source) {
        return gson.fromJson(source, Message.class);
    }

    public String serializeMessage() {
        return gson.toJson(this);
    }
}
