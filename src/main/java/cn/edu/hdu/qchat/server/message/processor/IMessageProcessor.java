package cn.edu.hdu.qchat.server.message.processor;

import cn.edu.hdu.qchat.server.Message;

/**
 * @author yejinbiao
 * @create 2017-02-10-上午9:26
 */

public interface IMessageProcessor {
    void process(Message message);
}
