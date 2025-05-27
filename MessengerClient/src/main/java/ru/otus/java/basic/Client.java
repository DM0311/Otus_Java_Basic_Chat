package ru.otus.java.basic;

import lombok.Getter;
import lombok.Setter;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.handlres.MessageHandler;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.processors.InputTextProcessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Socket socket = new Socket("localhost", 8090);
    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    DataInputStream in = new DataInputStream(socket.getInputStream());
    @Getter
    String userName;
    @Getter
    String currentRoomName;
    @Getter
    @Setter
    boolean isActive = true;
    boolean isAuthenticated;
    MessageHandler messageHandler;
    Scanner scanner;

    public Client() throws IOException {
        this.messageHandler = new MessageHandler();
        this.scanner = new Scanner(System.in);
        try {
            new Thread(() -> {
                try {
                    while (isActive) {
                        Message message = Message.parseMessage(in.readUTF());
                        if (message.getToUserName() != null) {
                            userName = message.getToUserName();
                            isAuthenticated = true;
                        }
                        if (message.getRoomName() != null) {
                            currentRoomName = message.getRoomName();
                        }
                        messageHandler.processMessage(message, this);
                        if (!isActive) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("___FFF___");
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();

            while (isActive) {

                String inputText = scanner.nextLine();
                Message message = InputTextProcessor.processInput(inputText, isAuthenticated);
                message.setFromUserName(userName);
                message.setRoomName(currentRoomName);
                out.writeUTF(message.serializeMessage());
                if (message.getCommand().equals(Commands.EXIT)) {
                    break;
                }
            }

        } catch (Exception e) {
            if (!isActive) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {


        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        try {
            System.in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
