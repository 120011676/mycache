package com.github.q120011676.mycache.core;

import java.io.Serializable;

/**
 * Created by say on 4/17/16.
 */
public class Command implements Serializable {
    private String operator;
    private String region;
    private Object key;
    private Object value;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
