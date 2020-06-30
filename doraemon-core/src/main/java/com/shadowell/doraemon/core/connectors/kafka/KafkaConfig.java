package com.shadowell.doraemon.core.connectors.kafka;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class KafkaConfig {

	private String bootstrapServers;
	private String topic;

	public KafkaConfig() {
	}

	public KafkaConfig(String bootstrapServers, String topic ) {
		this.bootstrapServers = bootstrapServers;
		this.topic = topic;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public void setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
