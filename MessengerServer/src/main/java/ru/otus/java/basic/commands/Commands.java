package ru.otus.java.basic.commands;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum Commands {
    AUTH("/auth", 2),
    REGISTER("/register", 3),
    EXIT("/exit", 0),
    DIRECT_MESSAGE("/w", 2),
    BROADCAST_MESSAGE("/b", 0),
    CHAT_MESSAGE("/ch", 0),
    BAN("/kick", 1),
    SHUT_DOWN("/shut_down", 0),
    ACTIVE_USERS("/active", 0),
    UNSUPPORTED("/unsupported", 0),
    DEFAULT("/default", 0);

    @Getter
    private final String commandCode;
    @Getter
    private final int numberOfParams;
    private static final Map<String, Commands> map;

    static {
        map = new HashMap<>();
        for (Commands value : Commands.values()) {
            map.put(value.getCommandCode(), value);
        }
    }

    Commands(String commandCode, int numberOfParams) {
        this.commandCode = commandCode;
        this.numberOfParams = numberOfParams;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public static Commands enumForValue(String value) {
        return map.get(value);
    }
}
