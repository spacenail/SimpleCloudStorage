package com.github.spacenail.SimpleCloudStorage.model;

public class RegRequest implements CloudMessage{
    private String username;
    private String password;

    public RegRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.REG_REQUEST;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
