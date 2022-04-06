package com.github.spacenail.SimpleCloudStorage.server;

import java.sql.*;

public class DataBase {
    private Connection connection;

    public DataBase() {
        try {
            connect();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/users.db");
    }

    void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public boolean select(String user, String password) {
        try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?")) {
            selectStatement.setString(1, user);
            selectStatement.setString(2, password);
            ResultSet resultSet = selectStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }
}