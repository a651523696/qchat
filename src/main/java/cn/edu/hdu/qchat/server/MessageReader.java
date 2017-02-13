package cn.edu.hdu.qchat.server;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yejinbiao
 * @create 2017-02-09-下午2:44
 */
@Setter
@Getter
public class MessageReader {
    private MessageBuffer messageBuffer;
    private List<Message> completeMessages;
    //只存取了临时数据
    private Message currentMessage;

    public MessageReader(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
        completeMessages = new ArrayList<>();
        currentMessage = messageBuffer.getNewMessage();
    }

    public void read(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        if (!byteBuffer.hasRemaining()) {
            byteBuffer.clear();
            return;
        }

        currentMessage.writeToMessage(byteBuffer);

        int endIndex = parseMessage(currentMessage);

        if (endIndex != -1) {
            Message message = messageBuffer.getNewMessage();
            currentMessage.writePartialMessageToMessage(message, endIndex);
            completeMessages.add(currentMessage);
            currentMessage = message;
        }
    }

    /**
     *
     * @param message the message which is to be parsed
     * @return return the endIndex of a full message in the @param message, return -1 if no full message find
     */
    private int parseMessage(Message message) {
        byte [] array = message.getInternalArray();
        for (int i = 0;i < message.getLength();i ++) {
            if (array[i] == '\n' && array[i - 1] == '\r') {
                return i;
            }
        }
        return -1;
    }
}
