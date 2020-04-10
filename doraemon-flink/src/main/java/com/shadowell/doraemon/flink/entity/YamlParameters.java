package com.paic.app.entity;

import com.paic.app.config.EngineConfig;
import com.paic.app.config.RuleConfig;
import com.paic.app.config.SinkConfig;
import com.paic.app.config.SourceConfig;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class YamlParameters {

	private RuleConfig ruleConfig;
	private SourceConfig sourceConfig;
	private SinkConfig sinkConfig;
	private EngineConfig engineConfig;

	public RuleConfig getRuleConfig() {
		return ruleConfig;
	}

	public void setRuleConfig(RuleConfig ruleConfig) {
		this.ruleConfig = ruleConfig;
	}

	public SourceConfig getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(SourceConfig sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	public SinkConfig getSinkConfig() {
		return sinkConfig;
	}

	public void setSinkConfig(SinkConfig sinkConfig) {
		this.sinkConfig = sinkConfig;
	}

	public EngineConfig getEngineConfig() {
		return engineConfig;
	}

	public void setEngineConfig(EngineConfig engineConfig) {
		this.engineConfig = engineConfig;
	}
}
