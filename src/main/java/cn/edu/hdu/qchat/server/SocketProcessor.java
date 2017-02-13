package cn.edu.hdu.qchat.server;

import cn.edu.hdu.qchat.util.UUIDGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;


/**
 * @author yejinbiao
 * @create 2017-02-09-上午10:47
 */
@Slf4j
@Setter
@Getter
public class SocketProcessor implements Runnable{
    private MessageProcessorChain messageProcessorChain;
    private Queue<Socket> inboundSockets;
    private Queue<Message> outboundMessages;
    private MessageBuffer messageBuffer;
    private WriterProxy writerProxy;
    private Selector writeSelector;
    private Selector readSelector;
    private Set<Socket> emptySockets = new HashSet<>();
    private Set<Socket> nonEmptySockets = new HashSet<>();
    public SocketProcessor(Queue<Socket> inboundSockets, Queue<Message> outboundMessages, MessageBuffer messageBuffer)
    throws IOException{
        this.messageProcessorChain = new MessageProcessorChain(this);
        this.inboundSockets = inboundSockets;
        this.outboundMessages = outboundMessages;
        this.messageBuffer = messageBuffer;
//        this.writerProxy = new WriterProxy(messageBuffer, outboundMessages);
        this.writeSelector = Selector.open();
        this.readSelector = Selector.open();
        this.socketMap = new HashMap<>();
    }

    private Map<String, Socket> socketMap;

    public Queue<Socket> getInboundSockets() {
        return inboundSockets;
    }

    public void setInboundSockets(Queue<Socket> inboundSockets) {
        this.inboundSockets = inboundSockets;
    }

    @Override
    public void run() {
        while (true) {
            processSockets();
            readFromSockets();
            try {

                writeToSockets();
            }catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToSockets() throws IOException{
        cancelEmptySockets();
        registerNonEmptySockets();
        int writeReadys = this.writeSelector.selectNow();
        if (writeReadys > 0) {
            Set<SelectionKey> keys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                Socket socket = (Socket) key.attachment();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                socket.write(byteBuffer);
                if (socket.getMessageWriter().isEmpty()) {
                    addToEmptySockets(socket);
                }
                it.remove();
            }
            keys.clear();
        }
    }

    private void cancelEmptySockets() {
        emptySockets.stream().forEach(socket -> {
            socket.getSocketChannel().keyFor(this.writeSelector).cancel();
        });
        emptySockets.clear();
    }
    private void registerNonEmptySockets() throws ClosedChannelException{
        nonEmptySockets.stream().forEach(socket -> {
            try {
                socket.getSocketChannel().register(this.writeSelector, SelectionKey.OP_WRITE, socket);
            }catch (ClosedChannelException e) {
                log.error("注册写事件时通道已关闭");
            }
        });
        nonEmptySockets.clear();
    }

    private void processSockets() {
        Socket newSocket = inboundSockets.poll();
        if (newSocket != null) {
            String socketId = UUIDGenerator.nextUUID();
            newSocket.setSocketId(socketId);
            //todo 分配reader和writer
            newSocket.setMessageReader(new MessageReader(this.messageBuffer));
            newSocket.setMessageWriter(new MessageWriter());
            socketMap.put(socketId, newSocket);
            try {

                newSocket.getSocketChannel().register(readSelector, SelectionKey.OP_READ, newSocket);
            } catch (ClosedChannelException e) {
                log.error("通道已关闭，无法注册");
            }
        }
    }

    private void readFromSockets() {
        try {
            int readReadys = this.readSelector.selectNow();
            if (readReadys > 0) {
                Iterator<SelectionKey> keys  = this.readSelector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    readFromSocket(key);
                    keys.remove();
                }
            }
        }catch (IOException e) {
            System.out.println(e);
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        Socket socket = (Socket) key.attachment();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socket.read(byteBuffer);

        List<Message> fullMessages = socket.getFullMessages();
        for (Message message : fullMessages) {
            //todo attach message with current socket's socketId
            this.messageProcessorChain.doProcess(message);
        }
        fullMessages.clear();
    }

    public void addToNonEmptySockets(Socket socket) {
        this.nonEmptySockets.add(socket);
    }

    public void removeFromNonEmptySockets(Socket socket) {
        this.nonEmptySockets.remove(socket);
    }
    public void removeFromEmptySockets(Socket socket) {
        this.emptySockets.remove(socket);
    }
    public void addToEmptySockets(Socket socket) {
        this.emptySockets.add(socket);
    }

}
