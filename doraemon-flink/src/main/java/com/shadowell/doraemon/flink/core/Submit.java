package com.shadowell.doraemon.flink.core;

import com.shadowell.doraemon.flink.config.FlinkConfig;
import com.shadowell.doraemon.flink.entity.YamlParameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/19
 */
public class Submit {

	private static final Logger logger = LoggerFactory.getLogger(Submit.class);

	public static void copy(String source, String destination) throws Exception {
		File file = new File(destination);
		if (!file.exists()) file.mkdirs();
		String script = "cp " + source + " " + destination;
		Executor.execute(script);
	}

	public static void packageJar(String destJarPath, String paramFilePath) throws Exception {
		String script = "jar uf " + destJarPath + " " + paramFilePath;
		Executor.execute(script);
	}

	public static void submitJar(YamlParameters prop) throws Exception {
		FlinkConfig flinkConfig = prop.getEngineConfig().getFlink();
		List<String> flinkSubmitProp = new ArrayList<>();
		flinkSubmitProp.add(flinkConfig.getFlinkHome() + "/bin/flink run");
		flinkSubmitProp.add("-p flinkConfig.getParallelism().toString()");
		flinkSubmitProp.add(flinkConfig.getJarPath() + "/" + flinkConfig.getJarName());

		//传递ConditionsPattern类
		flinkSubmitProp.add("-cp com.paic.engin.core.ConditionPattern");
		//传递ActionsPattern类
		flinkSubmitProp.add("-ap com.paic.engine.core.ActionPattern");

		String script = StringUtils.join(flinkSubmitProp.toArray(), " ");
		Executor.execute(script);
	}
}
