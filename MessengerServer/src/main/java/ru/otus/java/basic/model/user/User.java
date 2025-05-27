package ru.otus.java.basic.model.user;

import lombok.Data;

@Data
public class User {
    private Long lastActivity;
    private String login;
    private String password;
    private String username;
    private Role role;
}

