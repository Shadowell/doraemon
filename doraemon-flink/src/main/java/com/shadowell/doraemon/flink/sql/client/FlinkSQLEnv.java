package com.shadowell.doraemon.flink.sql.client;
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

import com.shadowell.doraemon.flink.utils.SQLUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class FlinkSQLEnv {

	private final static Logger logger = LoggerFactory.getLogger(FlinkSQLEnv.class);

	private static LinkedList<Map<String, Object>> tables = new LinkedList<Map<String, Object>>();
	private static HashMap<String, Object> execution = new HashMap<>();
	private static HashMap<String, Object> configuration = new HashMap<>();
	private static HashMap<String, Object> dataSource = new HashMap<>();
	private static LinkedList<String> query = new LinkedList<>();
	private static String name = "";

	private static StreamExecutionEnvironment env;
	private static StreamTableEnvironment tableEnv;

	public static void run(Boolean compile) throws Exception {
		if (StringUtils.isEmpty(name) && !compile) {
			throw new Exception("Name can not be null");
		}

		env = StreamExecutionEnvironment.getExecutionEnvironment();
		EnvironmentSettings settings = EnvironmentSettings
			.newInstance()
			.useBlinkPlanner()
			.inStreamingMode()
			.build();

		tableEnv = StreamTableEnvironment.create(env, settings);

		// register functions
		UDFunctons.register(tableEnv, "udf_");

		// get sql list


		// set configuration



	}

	public static List<SQLCommandCall> getCmdCallList {
		for (String sql : sqlList()) {
			parseSQLToCmdCall(sql);
		}
	}

	public static SQLCommandCall parseSQLToCmdCall(String sql) {
		Optional<SQLCommandCall> call = SQLCommand.parse(sql);
		if (call.isEmpty) {
			throw new RuntimeException("Can not parse sql : " + sql);
		}

		call.get();
	}

	public static List<String> sqlList() {
		List<String> sqlList = new ArrayList<String>();
		// load catalog
		String catalogSQL = catalog();
		if (StringUtils.isNotEmpty(catalogSQL)) {
			logger.info("Load catalog SQL file" + catalogSQL);
			query.addFirst(catalogSQL);
		}

		for (String query: query) {
			if (query.endsWith(".sql")) {
				sqlList.addAll(SQLUtil.readSQLFromFile(query));
			} else {
				sqlList.addAll(SQLUtil.readSQLFromFile(query));
			}
		}
		return sqlList;
	}

	public static String catalog() {
		String path = SQLUtil.getJarDir + "/config/catalog.sql";
		File file = new File(path);
		if (file.exists() && file.canRead()) {
			return path;
		} else {
			return null;
		}
	}
}
