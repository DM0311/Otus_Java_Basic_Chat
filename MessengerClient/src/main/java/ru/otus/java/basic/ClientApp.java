package ru.otus.java.basic;

import java.io.IOException;

public class ClientApp {
    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}