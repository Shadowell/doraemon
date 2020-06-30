package com.shadowell.doraemon.core.connectors.kafka;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */

public class KafkaOptions {

    public static final String BOOTSRAP_SERVER = "bootstrap.servers";
    public static final String GROUP_ID = "group.id";
    public static final String ENABLE_AUTO_COMMIT = "enable.auto.commit";
	public static final String AUTO_COMMIT_INTERVAL_MS = "auto.commit.interval.ms";
    public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
    public static final String SESSION_TIMEOUT = "session.timeout";
    public static final String KEY_DESERIALIZER = "key.deserializer";
    public static final String VALUE_DESERIALIZER = "value.deserializer";
    public static final String COMPRESSION_TYPE = "compression.type";
    public static final String BATCH_SIZE = "batch.size";
    public static final String ACKS = "acks";
}
