package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.AuthResponse;
import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private AuthController authController;

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage message) {
        if (message.getMessageType() == MessageType.AUTH_RESPONSE) {
            AuthResponse authResponse = (AuthResponse) message;
            authController.auth(authResponse.isAuth());
        }
    }
}
