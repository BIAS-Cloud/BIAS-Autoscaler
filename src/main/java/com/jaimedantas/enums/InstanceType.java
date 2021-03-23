package com.jaimedantas.enums;

import java.util.HashMap;
import java.util.Map;

public enum InstanceType {

    ONDEMAND("ondemand"),
    BURSTABLE("burstable");

    private static final Map<String, InstanceType> BY_LABEL = new HashMap<>();

    static {
        for (InstanceType e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public final String label;

    private InstanceType(String label) {
        this.label = label;

    }

    public static InstanceType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

}
