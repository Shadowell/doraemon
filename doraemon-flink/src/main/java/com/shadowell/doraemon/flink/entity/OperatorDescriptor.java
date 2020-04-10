package com.paic.app.entity;

import com.paic.app.core.Comparators;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/17
 */
public class OperatorDescriptor {

    private double fieldValue;
    private Comparators comparator;
    private double threshold;

    public double getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(double fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Comparators getComparator() {
        return comparator;
    }

    public void setComparator(Comparators comparator) {
        this.comparator = comparator;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}

