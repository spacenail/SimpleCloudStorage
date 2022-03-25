package com.github.spacenail.SimpleCloudStorage.server;

import com.github.spacenail.SimpleCloudStorage.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage> {
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
        switch (cloudMessage.getMessageType()) {
            case FILE:
                log.trace("incoming FILE message");
                FileMessage fileMessage = (FileMessage) cloudMessage;
                Files.write(Paths.get(fileMessage
                                .getPath())
                                .resolve(fileMessage.getName())
                        , fileMessage.getBytes()
                );
                ctx.writeAndFlush(new ListMessage(Paths.get(fileMessage
                        .getPath()))
                );
                break;
            case FILE_REQUEST:
                log.trace("incoming FILE_REQUEST message");
                FileRequestMessage fileRequestMessage = (FileRequestMessage) cloudMessage;
                Path path = Paths.get(fileRequestMessage.getReqPath());
                ctx.writeAndFlush(new FileMessage(path, fileRequestMessage.getDstPath()));
                break;
            case LIST_REQUEST:
                log.trace("incoming LIST_REQUEST message");
                ListRequestMessage listRequestMessage = (ListRequestMessage) cloudMessage;
                Path pathRequest = Paths.get(listRequestMessage.getPath(),
                        listRequestMessage.getResolve());
                if (pathRequest.toAbsolutePath().toFile().isDirectory()) {
                    ctx.writeAndFlush(new ListMessage(pathRequest.toAbsolutePath()));
                }
                break;
        }
    }
}