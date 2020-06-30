package com.shadowell.doraemon.flink;

import com.shadowell.doraemon.flink.config.FlinkConfig;
import com.shadowell.doraemon.flink.config.KafkaConfig;
import com.shadowell.doraemon.flink.config.TiDBConfig;
import com.shadowell.doraemon.flink.core.ActionsPattern;
import com.shadowell.doraemon.flink.core.Rule;
import com.shadowell.doraemon.flink.core.YamlBuilder;
import com.shadowell.doraemon.flink.entity.LoginEvent;
import com.shadowell.doraemon.flink.entity.LoginWarning;
import com.shadowell.doraemon.flink.entity.RulesEntity;
import com.shadowell.doraemon.flink.entity.YamlParameters;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */
public class FlinkApplication {

    public static void main(String[] args) throws Exception {

        final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);

        logger.info("Starting Flink Application ...");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		//final ParameterTool params = ParameterTool.fromArgs(args);

		//从jar包中获取YAML
		YamlParameters prop = YamlBuilder.build(FlinkApplication.class.getResourceAsStream("/engine.yaml"));

		TiDBConfig tiDBConfig = prop.getRuleConfig().getTiDBConfig();
		KafkaConfig kafkaSourceConfig = prop.getSourceConfig().getKafka();
		KafkaConfig kafkaSinkConfig = prop.getSinkConfig().getKafka();
		FlinkConfig flinkConfig = prop.getEngineConfig().getFlink();

        env.setStateBackend((StateBackend) new FsStateBackend(flinkConfig.getCheckpointPath()));
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.getCheckpointConfig().setCheckpointInterval(60 * 60 * 1000);
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


//        Properties sourceProperties = KafkaProperties.getConsumerProperties(kafkaSourceConfig);
//        FlinkKafkaConsumer010<LoginEvent> consumer = new FlinkKafkaConsumer010<LoginEvent>(
//			kafkaSourceConfig.getTopic(), new JSONKeyValueDeserializationSchema(), sourceProperties);
//
//        Properties sinkProperties = KafkaProperties.getProducerProperties(kafkaSinkConfig);
//        FlinkKafkaProducer010<LoginWarning> producer = new FlinkKafkaProducer010<LoginWarning>(
//                kafkaSinkConfig.getTopic(), new JSONKeyValueDeserializationSchema(), sinkProperties);

//        DataStream<LoginEvent> inputStream = env.addSource(consumer).setParallelism(4);


		DataStream<LoginEvent> inputStream = env.fromCollection(Arrays.asList(
			new LoginEvent("1","192.168.0.1","fail"),
			new LoginEvent("1","192.168.0.2","fail"),
			new LoginEvent("1","192.168.0.3","fail"),
			new LoginEvent("2","192.168.10,10","success"),
			new LoginEvent("2", "192.168.10.21", "success"),
			new LoginEvent("2", "192.168.10.22", "success")

		));

		//从配置文件加载规则
		RulesEntity rulesEntity = Rule.load(FlinkApplication.class.getResourceAsStream("/rule.json"));

		//从数据库定期读取规则
		boolean isLoad = true;

		Set<RulesEntity> rules = Rule.loadRuleFromTiDB(tiDBConfig);
		//构建Pattern
		Set<Pattern> patterns = Rule.build(rules);
		patterns.forEach(pattern -> {
			//构建PatternStream
			PatternStream<LoginEvent> patternStream =
				CEP.pattern(inputStream.keyBy(LoginEvent::getUserId), pattern);

			//构建SelectPatternStream
			DataStream<LoginWarning> outputStream = patternStream.select((Map<String, List<LoginEvent>> matchPattern) -> {
				List<LoginEvent> begin = ActionsPattern.selectPattern(matchPattern, "begin");
				return new LoginWarning(begin.get(0).getUserId(), begin.get(0).getIp(), begin.get(0).getType());
			});

			//Sink输出
			//loginSuccessDataStream.addSink(producer);
			outputStream.print();
		});

        env.execute();
    }
}
