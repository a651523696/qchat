package cn.edu.hdu.qchat.server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * @author yejinbiao
 * @create 2017-02-09-上午10:38
 */
@Setter
@Getter
@Slf4j
public class SocketAcceptor implements Runnable{
    private Queue<Socket> inboundSockets;

    private int tcpPort;

    private ServerSocketChannel serverSocketChannel;

    public SocketAcceptor() {
    }
    public SocketAcceptor(int tcpPort, Queue<Socket> inboundSockets) {
        this.tcpPort = tcpPort;
        this.inboundSockets = inboundSockets;
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(this.tcpPort));
        }catch (IOException e) {
            log.error("open server fail,reason:{}",e.getCause());
        }


        while (true) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                log.info("socket accepted:{}",socketChannel);
                socketChannel.configureBlocking(false);
                this.inboundSockets.add(new Socket(socketChannel));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
