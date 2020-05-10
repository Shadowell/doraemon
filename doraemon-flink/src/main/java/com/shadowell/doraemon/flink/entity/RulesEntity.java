package com.shadowell.doraemon.flink.entity;


import java.util.List;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/13
 */
public class RulesEntity {

    private String strategyID;
    private String version;
    private String status;
    private Integer event_interval;
    private List<RuleUnit> rules;

    public String getStrategyID() {
        return strategyID;
    }

    public void setStrategyID(String strategyID) {
        this.strategyID = strategyID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEvent_interval() {
        return event_interval;
    }

    public void setEvent_interval(Integer event_interval) {
        this.event_interval = event_interval;
    }

    public List<RuleUnit> getRules() {
        return rules;
    }

    public void setRules(List<RuleUnit> rules) {
        this.rules = rules;
    }
}
