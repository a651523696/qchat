package cn.edu.hdu.qchat.server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午9:59
 */

public class Server {
    private SocketAcceptor socketAcceptor;
    private SocketProcessor socketProcessor;
    private int tcpPort;
    public Server(int tcpPort) {
        this.tcpPort = tcpPort;
    }
    public void start() throws IOException{
        Queue<Socket> inboundSockets = new ArrayBlockingQueue<Socket>(1024);
        socketAcceptor = new SocketAcceptor(this.tcpPort, inboundSockets);
        Queue<Message> outboundMessage = new ArrayBlockingQueue<Message>(1024);
        MessageBuffer messageBuffer = new MessageBuffer();
        socketProcessor = new SocketProcessor( inboundSockets, outboundMessage, messageBuffer);
        new Thread(socketAcceptor).start();
        new Thread(socketProcessor).start();
    }
}
