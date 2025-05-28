package ru.otus.java.basic;

public class ServerApp {
    public static void main(String[] args) {
        new Server(8090).start();
    }
}