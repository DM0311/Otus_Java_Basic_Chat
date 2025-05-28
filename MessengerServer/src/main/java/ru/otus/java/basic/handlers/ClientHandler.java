package ru.otus.java.basic.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.Server;
import ru.otus.java.basic.commands.Commands;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.user.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.NoSuchElementException;


public class ClientHandler implements Runnable {
    private Socket socket;
    @Getter
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    @Setter
    @Getter
    private User user;
    @Setter
    @Getter
    private boolean isAuthenticated;
    @Setter
    @Getter
    private boolean working;
    private final Logger log;
    private final MessageHandler messageHandler;
    private final Gson gson;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.log = LogManager.getLogger(ClientHandler.class.getName());
        this.messageHandler = new MessageHandler();
        this.user = new User();
        this.working = true;
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        log.info("Отключен клиент: {}", this.user.getUsername());
    }

    @Override
    public void run() {
        try {

            while (working && !isAuthenticated) {
                Message startMsg = new Message();
                startMsg.setFromUserName("SERVER");
                startMsg.setCommand(Commands.DEFAULT);
                Instant instant = Instant.now();
                startMsg.setTimeStamp(instant.getEpochSecond());
                startMsg.setText("Перед работой с чатом необходимо выполнить аутентификацию "
                        + "/auth Login Password " +
                        "или регистрацию /reg Login Password UserName");
                startMsg.setCommand(Commands.DEFAULT);
                sendMsg(gson.toJson(startMsg));
                Message message = Message.parseMessage(in.readUTF());
                messageHandler.processMessage(message, this);
            }

            log.info("Клиент " + user.getUsername() + " прошел аутентификацию и подключился ...");

            while (working && isAuthenticated) {
                Message message = Message.parseMessage(in.readUTF());
                user.setLastActivity(message.getTimeStamp());
                messageHandler.processMessage(message, this);
            }

        } catch (IOException | NoSuchElementException e) {
            if(working){
                e.printStackTrace();
            }

        } finally {
            disconnect();
        }
    }
}
