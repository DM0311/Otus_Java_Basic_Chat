package ru.otus.java.basic.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.model.user.Role;
import ru.otus.java.basic.model.user.User;

import java.sql.*;

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
                    "u.role from console_chat.users u where u.login = ?");
            ps.setString(1, user.getLogin());
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false) {
                user.setUsername(null);
                return false;
            } else {
                String password = rs.getString(1);
                String username = rs.getString(2);
                String role = rs.getString(3);
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
}
