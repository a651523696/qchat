package cn.edu.hdu.qchat.server;

import lombok.Getter;
import lombok.Setter;

import java.util.Queue;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午9:27
 */
@Getter
@Setter
public class WriterProxy {
    private Queue<Message> outboundMessages;
    private MessageBuffer messageBuffer;
    public WriterProxy(MessageBuffer messageBuffer, Queue<Message> outboundMessages) {
        this.outboundMessages = outboundMessages;
    }

    public boolean enqueue(Message message) {
        return this.outboundMessages.offer(message);
    }
}
