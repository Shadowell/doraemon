/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shadowell.doraemon.flink.sql.client;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowell.doraemon.flink.app.EngineRuleApplication;
import com.shadowell.doraemon.flink.utils.JsonUtil;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class SQLClient {

	private final static Logger logger = LoggerFactory.getLogger(SQLClient.class);

	public static void main(String[] args) throws Exception {
		CommandLine commandLine = parse(args);
		logger.info("Command Line: " + Arrays.toString(args));
		FlinkSQLEnv sqlEnv = null;
		if (commandLine.hasOption("job")) {
			// read job file to string
			String text = commandLine.getOptionValue("job").toString();
			sqlEnv = JsonUtil.modelMapper(text, FlinkSQLEnv);
		}

		if (commandLine.hasOption("name")) {
			sqlEnv.name = commandLine.getOptionValue("name");
		}

		if (commandLine.hasOption("sql")) {
			String[] sqls = commandLine.getOptionValues("sql");
			for(String sql: sqls) {
				sqlEnv.query.add(sql);
			}
		}

		if (commandLine.hasOption("conf")) {
			String[] confs = commandLine.getOptionValues("conf");
			sqlEnv.confuguration.putAll(confs);
		}

		if (commandLine.hasOption("c") || commandLine.hasOption("compiler")) {
			sqlEnv.run(true);
		}
	}

	public static CommandLine parse(String[] args) {
		CommandLine commandLine = null;
		BasicParser basicParser = new BasicParser();
		HelpFormatter helpFormatter = new HelpFormatter();
		Options options = initOptions();
		helpFormatter.setWidth(100);
		try {
			commandLine = basicParser.parse(options, args);
			if (commandLine.hasOption("h")) {
				helpFormatter.printHelp("flink-sql", options, true);
				System.exit(0);
			}
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			helpFormatter.printHelp("flink-sql", options, true);
			System.exit(0);
		}
		return commandLine;
	}

	public static Options initOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "Get Help");

		Option fileOption = new Option(null, "job", true, "Job Description File");
		fileOption.setRequired(false);
		options.addOption(fileOption);

		Option sqlOption = new Option(null, "sql", true, "SQL File Name");
		sqlOption.setArgs(Option.UNLIMITED_VALUES);
		sqlOption.setRequired(false);
		options.addOption(sqlOption);

		Option jobOption = new Option(null, "name", true, "Job Name");
		jobOption.setRequired(false);
		options.addOption(jobOption);

		Option confOption = new Option(null, "conf", true, "Configuration Param");
		confOption.setArgs(Option.UNLIMITED_VALUES);

		Option compilerOption = new Option("c", "compiler", false, "Just Compiler SQL");
		options.addOption(compilerOption);

		return options;
	}

	public static HashMap getSQLConf(String[] conf) {
		HashMap map = new HashMap();
		if (conf == null) {
			return map;
		}
		for (String s : conf) {
			String[] split = s.split("=");
			map.put(split[0].trim(), split[1].trim());
		}
		return map;
	}

}
