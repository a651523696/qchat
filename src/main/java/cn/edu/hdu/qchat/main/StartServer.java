package cn.edu.hdu.qchat.main;

import cn.edu.hdu.qchat.server.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午10:09
 */
@Slf4j
public class StartServer {
    public static void main(String [] args) throws IOException {

        Server server = new Server(9999);
        server.start();
        log.info("服务器已启动");
    }
}
