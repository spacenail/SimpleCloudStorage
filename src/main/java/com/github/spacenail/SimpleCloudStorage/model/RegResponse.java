package com.github.spacenail.SimpleCloudStorage.model;

public class RegResponse {
    private boolean successCreateUser;
    private String message;

    public RegResponse(boolean successCreateUser) {
        this.successCreateUser = successCreateUser;
    }
    public RegResponse(boolean successCreateUser, String message) {
        this.successCreateUser = successCreateUser;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessCreateUser() {
        return successCreateUser;
    }
}
