package cn.edu.hdu.qchat.server;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yejinbiao
 * @create 2017-02-09-上午9:47
 */
@Slf4j
public class MessageBuffer {
    private static int KB = 1024;

    private static int MB = 1024 * KB;

    private static int CAPACITY_SMALL = 4 * KB;
    private static int CAPACITY_MEDIUM = 128 * KB;
    private static int CAPACITY_LARGE = 1 * MB;

    public Message getNewMessage() {
        Message message = new Message(this);
        //默认给最小4kb的缓存空间
        message.setInternalArray(new byte[CAPACITY_SMALL]);
        message.setCapacity(CAPACITY_SMALL);
        message.setLength(0);
        return message;

    }
    public boolean expandMessage(Message message) {
        if (message.getCapacity() == CAPACITY_SMALL) {
            return moveMessage(message, new byte [CAPACITY_MEDIUM] );
        }else if (message.getCapacity() == CAPACITY_MEDIUM) {
            return moveMessage(message, new byte[CAPACITY_LARGE]);
        }
        return false;
    }

    private boolean moveMessage(Message message, byte [] newMessageBuffer) {
        System.arraycopy(message.getInternalArray(), 0, newMessageBuffer, 0, message.getLength());
        message.setInternalArray(newMessageBuffer);
        message.setCapacity(newMessageBuffer.length);
        message.setOffest(0);
        return true;
    }

}
