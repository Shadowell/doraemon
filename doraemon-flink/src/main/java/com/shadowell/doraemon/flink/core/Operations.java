package com.paic.app.core;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/17
 */
public enum Operations {

    BEGIN("begin"), NEXT("next"), FOLLOWED_BY("followedBy"), FOLLOWED_BY_ANY("followedByAny"),
    NOT_NEXT("notNext"), NOT_FOLLOWED_BY("notFollowedBy"), WITHIN("within");

    private final String name;

    Operations(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
