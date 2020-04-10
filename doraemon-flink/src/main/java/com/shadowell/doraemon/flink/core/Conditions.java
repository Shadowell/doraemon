package com.paic.app.core;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/17
 */
public enum  Conditions {


    WHERE("where"), OR("or"), AND("and"), UNTIL("until"), SUBTYPE("subtype"),
    ONE_OR_MORE("oneOrMore"), TIMES_OR_MORE("timesOrMore"), TIMES("times"),
    OPTIONAL("optional"), GREEDY("greedy");

    private final String name;

    Conditions(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
