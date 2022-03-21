package com.github.spacenail.SimpleCloudStorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Server {

    public static void main(String[] args) {
        Logger log = LogManager.getLogger(Server.class);
        EventLoopGroup connections = new NioEventLoopGroup(1);
        EventLoopGroup messages = new NioEventLoopGroup();
        final ChannelHandler serverHandler = new ServerHandler();
        try{
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(connections,messages);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addFirst("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast("encoder", new ObjectEncoder());
                socketChannel.pipeline().addLast("logic", serverHandler);
            }
        });
        log.trace("Server started");
        ChannelFuture channelFuture = serverBootstrap.bind(8189).sync();
        log.trace("Server wait connections...");
        channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e){
            log.fatal("Fatal error!");
        e.printStackTrace();
        }finally {
            connections.shutdownGracefully();
            messages.shutdownGracefully();
            log.trace("Server shutdown");
        }
        }
}
