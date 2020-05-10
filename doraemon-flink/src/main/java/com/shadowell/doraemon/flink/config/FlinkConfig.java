package com.shadowell.doraemon.flink.config;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class FlinkConfig {
	private String flinkHome;
	private String checkpointPath;
	private Integer parallelism;
	private String jarPath;
	private String jarName;


	public String getCheckpointPath() {
		return checkpointPath;
	}

	public void setCheckpointPath(String checkpointPath) {
		this.checkpointPath = checkpointPath;
	}

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getFlinkHome() {
		return flinkHome;
	}

	public void setFlinkHome(String flinkHome) {
		this.flinkHome = flinkHome;
	}

	public Integer getParallelism() {
		return parallelism > 0 ?parallelism: 1;
	}

	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}
}
