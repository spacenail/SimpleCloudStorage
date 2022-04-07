package com.github.spacenail.SimpleCloudStorage.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?")) {
            selectStatement.setString(1, user);
            ResultSet resultSet = selectStatement.executeQuery();
            if(resultSet.next()){
                String dbPassword = resultSet.getString("password");
                String salt = resultSet.getString("salt");
                if(dbPassword.equals(calcHash(password,salt))){
                    return true;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    private String calcHash(String password, String hexSalt) {
        byte[] salt = hexToByte(hexSalt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(salt);
        byte[] byteHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return byteToHex(byteHash);
    }

    private byte[] hexToByte(String hex){
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
    }

    private String byteToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}