package ru.ilmira;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.DownloadFileMessage;
import message.FileMessage;
import message.Message;
import message.TextMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.StringJoiner;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        TextMessage textMessage = new TextMessage();
        textMessage.setText("Hello!\n");
        ctx.writeAndFlush(textMessage);
        sendFileList(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg instanceof DownloadFileMessage) {
            var message = (DownloadFileMessage) msg;
            if (message.getFileName().trim().isEmpty()) {
                sendFileList(ctx);
                return;
            }
            File file = FileUtils.getFile(message.getFileName());
            if (!file.exists()) {
                TextMessage errorMessage = new TextMessage();
                errorMessage.setText("File " + message.getFileName() + " not found!");
                ctx.writeAndFlush(errorMessage);
                return;
            }
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                final FileMessage fileMessage = new FileMessage();
                byte[] content = new byte[(int) randomAccessFile.length()];
                randomAccessFile.read(content);
                fileMessage.setName(file.getName());
                fileMessage.setContent(content);
                ctx.writeAndFlush(fileMessage);
            } catch (IOException e) {
                e.printStackTrace();
                TextMessage errorMessage = new TextMessage();
                errorMessage.setText("ERROR: " + file.getName() + ": " + e.getMessage() + "\n");
                ctx.writeAndFlush(errorMessage);
            }
        }
        if (msg instanceof TextMessage) {
            var message = (TextMessage) msg;
            System.out.println("Message from client: " + message.getText());
        }
    }

    private void sendFileList(ChannelHandlerContext ctx) throws IOException {
        TextMessage textMessage = new TextMessage();
        StringJoiner joiner = new StringJoiner("", "Select the file to download...\n" + "To complete the work, type \"exit\" to exit.\n" + "Available files on server:\n", "");
        for (Path path : FileUtils.getFilePaths()) {
            String s = path.getFileName().toString() + "\n";
            joiner.add(s);
        }
        textMessage.setText(joiner.toString());
        ctx.writeAndFlush(textMessage);
    }
}