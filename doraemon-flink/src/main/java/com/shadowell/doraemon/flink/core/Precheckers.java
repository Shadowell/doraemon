package com.shadowell.doraemon.flink.core;

import javax.annotation.Nullable;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class Precheckers {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, @Nullable String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    private Precheckers() {}
}
