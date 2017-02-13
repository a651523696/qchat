package cn.edu.hdu.qchat.server.message.processor;

import cn.edu.hdu.qchat.server.Message;
import cn.edu.hdu.qchat.server.Socket;
import cn.edu.hdu.qchat.server.SocketProcessor;

import java.util.Collection;

/**
 * @author yejinbiao
 * @create 2017-02-10-下午4:45
 */

public class BroadCastMessageProcessor implements IMessageProcessor {

    private SocketProcessor socketProcessor;

    public BroadCastMessageProcessor(SocketProcessor socketProcessor) {
        this.socketProcessor = socketProcessor;
    }

    @Override
    public void process(Message message) {
        Collection<Socket> connectedSockets = socketProcessor.getSocketMap().values();
        connectedSockets.stream().forEach(socket -> {
            socket.getMessageWriter().enqueue(message);

            socketProcessor.removeFromEmptySockets(socket);
            socketProcessor.addToNonEmptySockets(socket);
        });
    }
}
