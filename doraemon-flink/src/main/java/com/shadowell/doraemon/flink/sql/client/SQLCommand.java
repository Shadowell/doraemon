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

import com.google.gson.internal.$Gson$Preconditions;
import com.shadowell.doraemon.flink.utils.SQLUtil;
import javafx.util.Pair;
import scala.Array;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class SQLCommand {

	public SQLCommand(String name, String regex, Array<String> converter) {
		int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
		Pattern pattern = Pattern.compile(regex, DEFAULT_PATTERN_FLAGS);
	}

	public static SQLCommand apply(String name, String regex, Array<String> converter) {
		return new SQLCommand(name, regex, converter);
	}

	public static List<String> operand(List<String> op, int... pos) {
		List<String> result = new ArrayList<String>();
		for( int i = 0; i < pos.length; i++) {
			result.add(op.get(i));
		}
		return result;
	}

	public static Optional<SQLCommandCall> parse(String cmd){
		String stmt = cmd.trim();
		if (stmt.endsWith(";")) {
			stmt = stmt.substring(0, stmt.length() - 1).trim();
		}
		Field[] fields = SQLCommand.class.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			switch (field.get(SQLCommand.class)) {
				case field: new ArrayList(){field};
				default: new ArrayList();
			}
		}
	}

	public class SQLCommandCall {

		private String realStmt;
		private List<String> operands;

		public SQLCommandCall(String stmt, List<String> operands) {
			this.realStmt = stmt;
			this.operands = operands;
		}

		public String get(int pos) {
			return operands.get(pos);
		}

		public String getFirst() {
			return operands.get(0);
		}

		public Pair<String, String> getPair() {
			return new Pair<>(get(0), get(1));
		}

		public String getSecond() {
			return get(1);
		}

		public int size() {
			return operands.size();
		}

		public String getRealStmt() {
			return realStmt;
		}

		public void replaceParams(Map<String, Object> params) {
			for (int i = 0; i < params.size(); i++) {
				operands.set(i, SQLUtil.substitutor(operands.get(i), params));
			}
			realStmt = SQLUtil.substitutor(realStmt, params);
		}
	}
}
