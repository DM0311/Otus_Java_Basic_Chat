package ru.otus.java.basic.model;

import com.google.gson.Gson;
import lombok.Data;
import ru.otus.java.basic.commands.Commands;

import java.util.List;

@Data
public class Message {
    private Long timeStamp;
    private String fromUserName;
    private Long chatId;
    private String toUserName;
    private Commands command;
    private List<String> parameters;
    private String text;

    public static Message parseMessage(String source) {
        Gson gson = new Gson();
        return gson.fromJson(source, Message.class);
    }
}
