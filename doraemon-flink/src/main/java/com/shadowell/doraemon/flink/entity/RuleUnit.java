package com.shadowell.doraemon.flink.entity;

import com.shadowell.doraemon.flink.core.Operations;

import java.util.List;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/13
 */
public class RuleUnit {

    private String id;
    private Operations combine;
    private List<OperatorDescriptor> conditions;
    private Quantifier quantifier;
    private List<String> joiners;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Operations getCombine() {
        return combine;
    }

    public void setCombine(Operations combine) {
        this.combine = combine;
    }

    public List<OperatorDescriptor> getConditions() {
        return conditions;
    }

    public void setConditions(List<OperatorDescriptor> conditions) {
        this.conditions = conditions;
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(Quantifier quantifier) {
        this.quantifier = quantifier;
    }

    public List<String> getJoiners() {
        return joiners;
    }

    public void setJoiners(List<String> joiners) {
        this.joiners = joiners;
    }
}
