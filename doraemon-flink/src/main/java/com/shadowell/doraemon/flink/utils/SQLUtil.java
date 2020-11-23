package com.shadowell.doraemon.flink.utils;
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

import com.shadowell.doraemon.flink.sql.client.SQLClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import scala.Array;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLUtil {

	public static List<String> readSQLFromFile(String filePath) {
		File file = new File(filePath);
		List<String> strList = new ArrayList<>();
		try {
			strList = FileUtils.readLines(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String sqlStr = "";
		List<String> list = new ArrayList<>();
		for (String str : strList) {
			if (!str.trim().startsWith("--")) {
				sqlStr += sqlStr.join(str, "\n");
			}
		}
		String[] sqlList = sqlStr.split(";\\s*\n");
		for (String sql : sqlList) {
			if (sql.trim().length() > 0) {
				sql.replaceAll("--.*\\n", "\n");
				list.add(sql);
			}
		}
		return list;
	}

	public static List<String> readSQLFromString(String string) {
		StringBuilder sqlStr = new StringBuilder();
		String[] strList = string.split("}\n");
		List<String> list = new ArrayList<>();
		for (String str : strList) {
			if (!str.trim().startsWith("--")) {
				sqlStr.append(sqlStr.toString().join(str, "\n"));
			}
		}
		String[] sqlList = sqlStr.toString().split(";\\s*\n");
		for (String sql : sqlList) {
			if (sql.trim().length() > 0) {
				sql.replaceAll("--.*\\n", "\n");
				list.add(sql);
			}
		}
		return list;
	}

	public static String substitutor(String stmt, Map<String, Object> params) {
		StrSubstitutor strSubstitutor = new StrSubstitutor(params);
		return strSubstitutor.replace(stmt);
	}

	public static String getJarDir() {
		String path = SQLUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("dows")) {
			path = path.substring(1, path.length());
		}
		if (path.contains("jar")) {
			path = path.substring(0, path.lastIndexOf("."));
			return path.substring(0, path.lastIndexOf("/"));
		}
		path = path.replace("target/classes/", "");
		return StringUtils.stripEnd(path, "/");
	}

	public void runJob(String sql, String name) throws Exception {
		String[] args = new String[]{ "--sql", sql, "--name", name};
		SQLClient.main(args);
	}
}
