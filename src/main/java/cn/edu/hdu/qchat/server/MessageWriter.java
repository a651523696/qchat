package cn.edu.hdu.qchat.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yejinbiao
 * @create 2017-02-09-下午2:43
 */

public class MessageWriter {
    private List<Message> writeQueue;
    private Message messageInProgress;
    private int writtenBytes;

    public MessageWriter () {
        this.writeQueue = new ArrayList<>();
    }
    public void enqueue(Message message) {
        if (messageInProgress == null) {
            messageInProgress = message;
        }else {
            writeQueue.add(message);
        }
    }
    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException{
        byteBuffer.put(messageInProgress.getInternalArray(), writtenBytes, messageInProgress.getLength() - writtenBytes);
        byteBuffer.flip();

        writtenBytes += socket.writeToChannel(byteBuffer);
        byteBuffer.clear();
        if (writtenBytes >= messageInProgress.getLength()) {
            if (writeQueue.size() > 0) {
                messageInProgress = writeQueue.remove(0);
            }else {
                messageInProgress = null;
            }
            writtenBytes = 0;
        }

    }

    public boolean isEmpty() {
        return this.writeQueue.isEmpty() && this.messageInProgress == null;
    }
}
