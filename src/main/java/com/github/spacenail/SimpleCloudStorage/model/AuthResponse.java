package com.github.spacenail.SimpleCloudStorage.model;

public class AuthResponse implements CloudMessage {
    private boolean isAuth;

    public boolean isAuth() {
        return isAuth;
    }

    public AuthResponse(boolean isAuth) {
        this.isAuth = isAuth;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH_RESPONSE;
    }
}
