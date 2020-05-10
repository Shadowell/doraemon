package com.shadowell.doraemon.flink.entity;

import com.shadowell.doraemon.flink.core.Conditions;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/17
 */
public class Quantifier {

    private Conditions type;
    private int value;

    public Conditions getType() {
        return type;
    }

    public void setType(Conditions type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
