package com.shadowell.doraemon.flink.app;

import com.shadowell.doraemon.flink.config.FileConfig;
import com.shadowell.doraemon.flink.config.FlinkConfig;
import com.shadowell.doraemon.flink.core.Submit;
import com.shadowell.doraemon.flink.core.YamlBuilder;
import com.shadowell.doraemon.flink.entity.YamlParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */
public class EngineRuleApplication {

    public static void main(String[] args) throws Exception {

        final Logger logger = LoggerFactory.getLogger(EngineRuleApplication.class);

        logger.info("Starting Engine Rule Application ...");

		YamlParameters prop = YamlBuilder.build();
		FileConfig fileConfig = prop.getRuleConfig().getFileConfig();
		FlinkConfig flinkConfig = prop.getEngineConfig().getFlink();

		//获取配置文件(规则文件,YAML文件)
		String stdJarPath = flinkConfig.getJarPath() + "/" + flinkConfig.getJarName();
		String fatJarPath = stdJarPath + "/tmp";
		String ruleFilePath = fileConfig.getRuleFilePath() + "/" + fileConfig.getRuleFileName();
		String yamlFilePath = "src/main/resources/engine/yaml";
		Submit.copy(stdJarPath, fatJarPath);
		Submit.packageJar(fatJarPath, ruleFilePath);
		Submit.packageJar(fatJarPath, yamlFilePath);
		Submit.submitJar(prop);
    }
}
