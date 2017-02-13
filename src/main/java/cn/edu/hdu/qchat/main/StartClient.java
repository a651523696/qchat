package cn.edu.hdu.qchat.main;

import cn.edu.hdu.qchat.client.Client;

import java.io.IOException;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午10:23
 */

public class StartClient {
    public static void main(String [] args) throws IOException {
        Client client = new Client("192.168.11.36",9999);
        client.start();
    }
}
