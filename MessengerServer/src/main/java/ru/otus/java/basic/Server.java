package ru.otus.java.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private int port;
    private ExecutorService executor;
    private DataBaseProvider dataBaseProvider;
    private ActiveUsersProvider activeUsersProvider;
    private final Logger log;
    private AtomicBoolean isOn;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(5);
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.log = LogManager.getLogger(Server.class.getName());
        this.isOn = new AtomicBoolean(true);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            log.info("Сервер запущен на порту " + port);
            while (isOn.get()) {
                Socket clientSocket = serverSocket.accept();
                log.debug("Получено подключение от клиента: {}", clientSocket.getLocalSocketAddress());
                ClientHandler client = new ClientHandler(clientSocket, this);
                executor.execute(client);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
            log.info("Сервер отключен командой от администратора");
        }
    }

    public void shutDown() {
        isOn.set(false);
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
