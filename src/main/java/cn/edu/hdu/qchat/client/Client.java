package cn.edu.hdu.qchat.client;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午10:17
 */
@Slf4j
public class Client {
    private SocketChannel socketChannel;
    private Selector readSelector;

    public Client(String host,int tcpPort) throws IOException {
        this.socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(host, tcpPort));
        readSelector = Selector.open();
        socketChannel.register(readSelector, SelectionKey.OP_CONNECT);
    }

    public void start() throws IOException {
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {

                while ((line = reader.readLine()) != null) {
                    line = line + "\r\n";
                    socketChannel.write(ByteBuffer.wrap(line.getBytes()));
                }
            } catch (IOException e) {
                System.out.println("io错误");
            }
        }).start();
        new Thread(new MessageReceiver(this.socketChannel)).start();

    }
    private class MessageReceiver  implements  Runnable{

        private SocketChannel socketChannel;

        public MessageReceiver(SocketChannel socketChannel) throws IOException {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
        try {
            while (true) {

                int selectedKeys = readSelector.select();
                if (selectedKeys < 1) {
                    continue;
                }
                Iterator<SelectionKey> keys = readSelector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isConnectable()) {
                        if (socketChannel.finishConnect()) {
                            socketChannel.register(readSelector, SelectionKey.OP_READ);
                        }
                    }else if (key.isReadable()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        SocketChannel channel = (SocketChannel) key.channel();
                        channel.read(byteBuffer);
                        String message = new String(byteBuffer.array()).trim();
                        System.out.println(message);
                    }
                }
            }
        }catch (IOException e) {
            System.out.println(e);
        }

        }
    }
}
