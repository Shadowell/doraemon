package com.shadowell.doraemon.kafka.utils;

import com.shadowell.doraemon.core.connectors.kafka.KafkaConfig;
import com.shadowell.doraemon.core.connectors.kafka.KafkaOptions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class KafkaUtils {

	public static void KProducer() {

	}

	public static void KProducer(KafkaConfig config, String message) {
		Properties properties = new Properties();
		properties.put(KafkaOptions.BOOTSRAP_SERVER, config.getBootstrapServers());
		properties.put(KafkaOptions.ACKS, "all");
		properties.put(KafkaOptions.COMPRESSION_TYPE, "gzip");
		properties.put(KafkaOptions.BATCH_SIZE, 1000);
		properties.put(KafkaOptions.KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
		properties.put(KafkaOptions.VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String,String> producer = new KafkaProducer<String, String>(properties);
		producer.send(new ProducerRecord<String, String>(config.getTopic(), message));
		producer.close();
	}

	public static void KConsumer() {

	}

	public static void KConsumer(KafkaConfig config) {
		Properties properties = new Properties();
		properties.put(KafkaOptions.BOOTSRAP_SERVER, config.getBootstrapServers());
		properties.put(KafkaOptions.GROUP_ID, "consumer-1");
		properties.put(KafkaOptions.ENABLE_AUTO_COMMIT, true);
		properties.put(KafkaOptions.AUTO_COMMIT_INTERVAL_MS, 1000);
		properties.put(KafkaOptions.AUTO_OFFSET_RESET, "latest");
		properties.put(KafkaOptions.SESSION_TIMEOUT, "30000");
		properties.put(KafkaOptions.KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
		properties.put(KafkaOptions.VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		consumer.subscribe(Arrays.asList(config.getTopic()));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, value = %s", record.offset(), record.value());
				System.out.println();
			}
		}
	}
}
