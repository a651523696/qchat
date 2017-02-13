package cn.edu.hdu.qchat.server;

import cn.edu.hdu.qchat.server.message.processor.BroadCastMessageProcessor;
import cn.edu.hdu.qchat.server.message.processor.IMessageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yejinbiao
 * @create 2017-02-10-下午4:40
 */

public class MessageProcessorChain {
    private List<IMessageProcessor> messageProcessors;

    public MessageProcessorChain (SocketProcessor socketProcessor) {
        messageProcessors = new ArrayList<>();
        messageProcessors.add(new BroadCastMessageProcessor(socketProcessor));
    }

    public void doProcess(Message message) {
        messageProcessors.stream().forEach(processor -> {
            processor.process(message);
        });
    }
}
