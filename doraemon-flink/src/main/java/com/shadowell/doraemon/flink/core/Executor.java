package com.shadowell.doraemon.flink.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/19
 */
public class Executor {

	public static void execute(String script)throws Exception {
		Process process;
		process = Runtime.getRuntime().exec(script);
		BufferedReader errorsReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		List<String> errorLines = new ArrayList<>();
		String error;
		do {
			error = errorsReader.readLine();
			if (error != null) {
				errorLines.add(error);
			}
		} while (error != null);
		int exitValue = process.waitFor();
		switch (exitValue) {
			case 0:
				return;
			default:
				throw new Exception("Execute script: " + script + " failed." + errorLines);
		}
	}
}

