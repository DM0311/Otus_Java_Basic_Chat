package ru.otus.java.basic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.java.basic.model.Message;

public class MessageSerializer {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static String serialize(Message msg){
        return gson.toJson(msg);
    }
}
