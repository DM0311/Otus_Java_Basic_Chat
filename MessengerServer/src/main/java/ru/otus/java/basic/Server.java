package ru.otus.java.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.providers.RoomProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private int port;
    private ExecutorService executor;
    private DataBaseProvider dataBaseProvider;
    private ActiveUsersProvider activeUsersProvider;
    private RoomProvider roomProvider;
    private final Logger log;
    private boolean isOn;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(5);
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.roomProvider = RoomProvider.getInstance();
        for(Room room : this.dataBaseProvider.getRooms()){
            roomProvider.addRoom(room);
        }
        this.log = LogManager.getLogger(Server.class.getName());
        this.isOn = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            log.info("Сервер запущен на порту " + port);
            while (isOn) {
                Socket clientSocket = serverSocket.accept();
                log.debug("Получено подключение от клиента: {}", clientSocket.getLocalSocketAddress());
                ClientHandler client = new ClientHandler(clientSocket, this);
                executor.execute(client);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

    public void shutDown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
