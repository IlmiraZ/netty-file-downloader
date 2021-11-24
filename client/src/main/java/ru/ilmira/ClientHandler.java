package ru.ilmira;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.FileMessage;
import message.Message;
import message.TextMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final String filesDir = "./client/files";
    private static final Path dirPath = Paths.get(filesDir);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof TextMessage) {
            var message = (TextMessage) msg;
            System.out.println(message.getText());
        }
        if (msg instanceof FileMessage) {
            var message = (FileMessage) msg;//
            Path path = Paths.get(dirPath.toFile().getPath(), message.getName());
            try (final RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "rw")) {
                randomAccessFile.write(message.getContent());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("The file " + message.getName() + " successfully uploaded to " + path);
        }
    }
}