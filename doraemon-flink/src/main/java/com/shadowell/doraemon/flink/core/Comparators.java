package com.paic.app.core;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/17
 */
public enum  Comparators {

    EQ("="), GT(">"), GE(">="), LT("<"), LE("<="), NEQ("!=");
    private final String name;

    Comparators(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
