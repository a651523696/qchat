package cn.edu.hdu.qchat.server;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author yejinbiao
 * @create 2017-02-09-上午10:25
 */
@Setter
@Getter
public class Socket {
    private String socketId;
    private SocketChannel socketChannel;

    private MessageReader messageReader;
    private MessageWriter messageWriter;

    private boolean closed;


    public Socket() {

    }

    public Socket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void read(ByteBuffer byteBuffer) throws IOException {
        this.readFromChannel(byteBuffer);
        this.messageReader.read(byteBuffer);
    }

    public void write(ByteBuffer byteBuffer) throws IOException {
        this.messageWriter.write(this, byteBuffer);
    }

    private int readFromChannel(ByteBuffer byteBuffer) throws IOException {
        int totalBytesRead = 0;
        int bytesRead = 0;
        while ((bytesRead = this.socketChannel.read(byteBuffer)) > 0) {

            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1) {
            closed = true;
        }
        return totalBytesRead;
    }

    public int writeToChannel(ByteBuffer byteBuffer) throws IOException {
        int totalBytesWrite = 0;
        int bytesWrite = 0;
        while (byteBuffer.hasRemaining()) {
            bytesWrite = this.socketChannel.write(byteBuffer);
            totalBytesWrite += bytesWrite;
        }
        return totalBytesWrite;
    }
    public List<Message> getFullMessages() {
        return this.messageReader.getCompleteMessages();
    }

}
