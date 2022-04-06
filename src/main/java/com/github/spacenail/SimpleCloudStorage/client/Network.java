package com.github.spacenail.SimpleCloudStorage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class Network implements Runnable {
    public final Bootstrap bootstrap;
    private final EventLoopGroup executors = new NioEventLoopGroup(1);
    private Channel channel;


    public Network(AuthController authController) {
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(executors);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addFirst("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast("encoder", new ObjectEncoder());
                socketChannel.pipeline().addLast("logic", new AuthHandler(authController));
            }
        });
    }

    public void send(Object msg) {
        channel.writeAndFlush(msg);
    }

    public void close() {
        executors.shutdownGracefully();
    }

    public void changeHandler(ChannelHandler newHandler) {
        channel.pipeline().remove("logic");
        channel.pipeline().addLast(newHandler);
    }

    @Override
    public void run() {
        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8189).sync();
            channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executors.shutdownGracefully();
        }
    }
}
