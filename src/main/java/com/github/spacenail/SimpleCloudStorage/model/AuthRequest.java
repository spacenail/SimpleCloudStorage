package com.github.spacenail.SimpleCloudStorage.model;

public class AuthRequest implements CloudMessage{
    private String login;
    private String password;

    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH_REQUEST;
    }
}
