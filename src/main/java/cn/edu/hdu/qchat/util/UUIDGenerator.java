package cn.edu.hdu.qchat.util;

import java.util.UUID;

/**
 * @author yejinbiao
 * @create 2017-02-09-下午2:23
 */

public class UUIDGenerator {
    public static String nextUUID() {
        return UUID.randomUUID().toString();
    }
}
