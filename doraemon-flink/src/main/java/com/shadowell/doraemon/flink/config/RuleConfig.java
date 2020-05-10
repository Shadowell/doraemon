package com.shadowell.doraemon.flink.config;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class RuleConfig {

	private FileConfig fileConfig;
	private TiDBConfig tiDBConfig;

	public FileConfig getFileConfig() {
		return fileConfig;
	}

	public void setFileConfig(FileConfig fileConfig) {
		this.fileConfig = fileConfig;
	}

	public TiDBConfig getTiDBConfig() {
		return tiDBConfig;
	}

	public void setTiDBConfig(TiDBConfig tiDBConfig) {
		this.tiDBConfig = tiDBConfig;
	}
}
