package cn.edu.hdu.qchat.server;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

/**
 * @author yejinbiao
 * @create 2017-02-09-上午10:08
 */
@Setter
@Getter
public class Message {
    private MessageBuffer messageBuffer;
    private byte[] internalArray;
    private int offest;
    private int capacity;
    private int length;

    public Message() {

    }

    public Message(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public int writeToMessage(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        if (remaining + length > capacity) {
            if (!messageBuffer.expandMessage(this)) {
                return -1;
            }
        }
        int bytesToCopy = Math.min(remaining, capacity - length);
        byteBuffer.get(this.internalArray, this.length, bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }
    public int writePartialMessageToMessage(Message message, int endIndex) {
        int startIndexOfPartialMessage = endIndex + 1;
        int lengthOfPartialMessage = this.length - startIndexOfPartialMessage;
        System.arraycopy(this.internalArray, startIndexOfPartialMessage, message.getInternalArray(), 0, lengthOfPartialMessage );
        this.length = startIndexOfPartialMessage;
        return lengthOfPartialMessage;
    }
}
