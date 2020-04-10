package com.paic.app.config;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class KafkaConfig {
	private String brokerList;
	private String topic;

	public String getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
