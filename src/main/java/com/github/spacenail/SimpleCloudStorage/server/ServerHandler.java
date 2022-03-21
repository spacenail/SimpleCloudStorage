package com.github.spacenail.SimpleCloudStorage.server;

import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileRequestMessage;
import com.github.spacenail.SimpleCloudStorage.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage>{
    private Path serverDirectory = Paths.get("ServerDirectory");
    private static final Logger log = LogManager.getLogger();

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.trace("Disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.trace("Connected: " + ctx.channel().remoteAddress());
        ctx.writeAndFlush(new ListMessage(serverDirectory));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
    switch (cloudMessage.getMessageType()){
        case FILE:
            FileMessage fileMessage = (FileMessage) cloudMessage;
            Files.write(serverDirectory.resolve(fileMessage.getName()),fileMessage.getBytes());
            ctx.writeAndFlush(new ListMessage(serverDirectory));
            break;
        case FILE_REQUEST:
            FileRequestMessage fileRequestMessage = (FileRequestMessage)cloudMessage;
            ctx.writeAndFlush(new FileMessage(serverDirectory.resolve(fileRequestMessage.getName())));
            break;
    }
    }
}