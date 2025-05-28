package ru.otus.java.basic.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.model.Message;
import ru.otus.java.basic.model.room.Room;
import ru.otus.java.basic.model.user.Role;
import ru.otus.java.basic.model.user.User;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DataBaseProvider {

    private static volatile DataBaseProvider instance;
    private Connection connection;
    private final Logger log;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/otus_chat";

    private DataBaseProvider() {
        this.log = LogManager.getLogger(DataBaseProvider.class);
        try {
            connection = DriverManager.getConnection(DATABASE_URL,
                    "chat_server",
                    "admin");
            log.info("Подключение к БД прошло успешно");
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД. Сообщение от БД: {} Код ошибки: {}",
                    e.getMessage(), e.getErrorCode());
            throw new RuntimeException(e);
        }
    }

    public static DataBaseProvider getInstance() {
        DataBaseProvider localInstance = instance;
        if (localInstance == null) {
            synchronized (DataBaseProvider.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DataBaseProvider();
                }
            }
        }
        return localInstance;
    }


    public boolean registerUser(User user) {
        try {
            ResultSet rs;
            PreparedStatement findLogin = connection.prepareStatement("select u.login from console_chat.users u where u.login = ?");
            findLogin.setString(1, user.getLogin());
            rs = findLogin.executeQuery();
            if (rs.next()) {
                user.setLogin(null);
                return false;
            }

            PreparedStatement findUserName = connection.prepareStatement("select u.user_name from console_chat.users u where u.user_name = ?");
            findUserName.setString(1, user.getUsername());
            rs = findUserName.executeQuery();
            if (rs.next()) {
                user.setUsername(null);
                return false;
            }

            PreparedStatement newUser = connection.prepareStatement("INSERT INTO console_chat.users " +
                    "(login, user_name, password, role) VALUES (?,?,?,?)");
            newUser.setString(1, user.getLogin());
            newUser.setString(2, user.getUsername());
            newUser.setString(3, user.getPassword());
            newUser.setString(4, Role.USER.getRoleName());
            newUser.execute();
            user.setRole(Role.USER);
            return true;
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean authenticateUser(User user) {
        try {
            PreparedStatement ps = connection.prepareStatement("select u.password, u.user_name, " +
                    "u.role, u.isbanned, u.ban_time, u.last_activity from console_chat.users u where u.login = ?");
            ps.setString(1, user.getLogin());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                user.setUsername(null);
                return false;
            } else {
                String password = rs.getString(1);
                String username = rs.getString(2);
                String role = rs.getString(3);
                boolean isBanned = rs.getBoolean(4);
                int minutes = rs.getInt(5);
                Instant ts = Instant.ofEpochSecond(rs.getLong(6));
                if(isBanned || ts.plus(minutes, ChronoUnit.MINUTES).isAfter(Instant.now())){
                    return false;
                }

                if (user.getPassword().equals(password)) {
                    user.setUsername(username);
                    user.setRole(Role.enumForValue(role));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public long getLastActivity(User user) {
        try {
            PreparedStatement ps = connection.prepareStatement("select u.last_activity from console_chat.users u " +
                    "where u.user_name = ?");
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return 0L;
            } else {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public void banUser(String username, int minutes) {
        try {
            PreparedStatement ps = connection.prepareStatement("update console_chat.users as u set isbanned  = ?, ban_time = ?  where u.user_name = ?");
            if (minutes == 0){
                ps.setBoolean(1, true);
                ps.setInt(2,0);
            }else {
                ps.setBoolean(1, false);
                ps.setInt(2,minutes);
            }
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public void setLastActivity(User user) {
        try {
            PreparedStatement ps = connection.prepareStatement("update console_chat.users as u set last_activity = ? where u.user_name = ?");
            ps.setLong(1, user.getLastActivity());
            ps.setString(2, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean nickIsUsed(String userName) {
        try {
            PreparedStatement ps = connection.prepareStatement("select u.user_name from console_chat.users u where u.user_name = ?");
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean changeNick(String oldUserName, String newUserName) {
        try {
            PreparedStatement ps = connection.prepareStatement("update console_chat.users as u set user_name = ? where u.user_name = ?");
            ps.setString(1, newUserName);
            ps.setString(2, oldUserName);
            int rowsNum = ps.executeUpdate();
            if (rowsNum > 0) {
                log.info("Пользователь {} сменил имя на {}", oldUserName, newUserName);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from console_chat.rooms r");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String roomName = rs.getString(1);
                String owner = rs.getString(2);
                String pwd = rs.getString(3);
                Long lastActivity = rs.getLong(4);
                Room r = new Room(roomName, pwd, owner);
                r.setLastActivity(lastActivity);
                rooms.add(r);
            }
            log.info("Сохранено комнат: {}", rooms.size());
            return rooms;
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean addRoom(Room newRoom) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO console_chat.rooms " +
                    "(name, owner, pwd, last_activity) VALUES (?, ?, ?, ?)");
            ps.setString(1, newRoom.getRoomName());
            ps.setString(2, newRoom.getOwner());
            ps.setString(3, newRoom.getRoomPassword());
            ps.setLong(4, newRoom.getLastActivity());
            ps.executeUpdate();
            log.info("Создана комната: {}", newRoom.getRoomName());
            return true;

        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean deleteRoom(Room r) {
        try {
            PreparedStatement ps = connection.prepareStatement("delete from console_chat.rooms r where name = ?");
            ps.setString(1, r.getRoomName());
            ps.executeUpdate();
            log.info("Удалена комната: {}", r.getRoomName());
            return true;

        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public void archiveMessage(Message msg) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO console_chat.room_history (room_name, time_stamp, message_text, from_user) VALUES (?, ?, ?, ?)");
            ps.setString(1, msg.getRoomName());
            ps.setLong(2, msg.getTimeStamp());
            ps.setString(3, msg.getText());
            ps.setString(4, msg.getFromUserName());
            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public List<Message> getLastMsgInRoom(String roomName) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT *\n" +
                    "FROM console_chat.room_history ch where room_name = ? \n" +
                    "ORDER BY ch.\"time_stamp\" ASC\n" +
                    "LIMIT 10;");
            ps.setString(1, roomName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setRoomName(roomName);
                msg.setTimeStamp(rs.getLong(2));
                msg.setText(rs.getString(3));
                msg.setFromUserName(rs.getString(4));
                messages.add(msg);
            }
        } catch (SQLException e) {
            log.error("Runtime exception", e);
            throw new RuntimeException(e);
        }
        return messages;
    }
}
