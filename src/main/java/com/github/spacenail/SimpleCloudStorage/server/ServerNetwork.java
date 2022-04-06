package com.github.spacenail.SimpleCloudStorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetwork {
    private EventLoopGroup connections;
    private EventLoopGroup messages;
    private Logger log;
    private ServerBootstrap serverBootstrap;
    private final DataBase db;

    public ServerNetwork() {
        networkInit();
        db = new DataBase();
        log.trace("Server started...");
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8189).sync();
            log.trace("Server wait connections...");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Interrupt");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            connections.shutdownGracefully();
            messages.shutdownGracefully();
            db.disconnect();
            log.trace("Server shutdown");
        }
    }

    private void networkInit() {
        connections = new NioEventLoopGroup(1);
        messages = new NioEventLoopGroup();
        log = LogManager.getLogger(Server.class);

        serverBootstrap = new ServerBootstrap()
                .group(connections, messages)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                                  @Override
                                  protected void initChannel(SocketChannel socketChannel) {
                                      socketChannel.pipeline().addFirst("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                                      socketChannel.pipeline().addLast("encoder", new ObjectEncoder());
                                      socketChannel.pipeline().addLast("logic", new ServerHandler(db));
                                  }
                              }
                );
    }
}