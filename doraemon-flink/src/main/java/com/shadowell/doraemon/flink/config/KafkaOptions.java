package com.shadowell.doraemon.flink.config;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */

public class KafkaOptions {

    public static final String BOOTSRAP_SERVER = "bootstrap.servers";
    public static final String GROUP_ID = "group.id";
    public static final String ENABLE_AUTO_COMMIT = "enable.auto.commit";

    public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
    public static final String SESSION_TIMEOUT = "session.timeout";
    public static final String KEY_DESERIALIZER = "key.deserializer";
    public static final String VALUE_DESERIALIZER = "value.deserializer";
}
