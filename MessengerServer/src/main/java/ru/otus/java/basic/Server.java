package ru.otus.java.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.handlers.ClientHandler;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.providers.ActiveUsersProvider;
import ru.otus.java.basic.providers.DataBaseProvider;
import ru.otus.java.basic.providers.RoomProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private final int port;
    private final ExecutorService clientRequestExecutor;
    private final ScheduledExecutorService serviceTasksExecutor;
    private DataBaseProvider dataBaseProvider;
    private ActiveUsersProvider activeUsersProvider;
    private RoomProvider roomProvider;
    private final Logger log;
    private boolean isOn;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        this.clientRequestExecutor = Executors.newFixedThreadPool(4);
        this.serviceTasksExecutor = Executors.newScheduledThreadPool(2);
        this.dataBaseProvider = DataBaseProvider.getInstance();
        this.activeUsersProvider = ActiveUsersProvider.getInstance();
        this.roomProvider = RoomProvider.getInstance();
        for (Room room : this.dataBaseProvider.getRooms()) {
            roomProvider.addRoom(room);
        }
        this.log = LogManager.getLogger(Server.class.getName());
        this.isOn = true;
    }

    public void start() {
        serviceTasksExecutor.scheduleAtFixedRate(() -> {
            activeUsersProvider.checkUsers();
            List<String> inactiveRooms = roomProvider.checkRoomsActivity();
            for (String element : inactiveRooms) {
                if(element.equals("Default")){
                    continue;
                }
                dataBaseProvider.deleteRoom(roomProvider.getRoom(element));
                roomProvider.deleteRoom(element);
            }
        }, 20, 5, TimeUnit.MINUTES);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            log.info("Сервер запущен на порту " + port);
            while (isOn) {
                Socket clientSocket = serverSocket.accept();
                log.debug("Получено подключение от клиента: {}", clientSocket.getLocalSocketAddress());
                ClientHandler client = new ClientHandler(clientSocket, this);
                clientRequestExecutor.execute(client);
            }

        } catch (Exception e) {
            if (!isOn) {
                log.info("Сервер был остановлен командой администратора");
            } else {
                log.error(e);
            }

        } finally {
            clientRequestExecutor.shutdownNow();
            serviceTasksExecutor.shutdownNow();
        }
    }

    public void shutDown() {
        try {
            isOn = false;
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
