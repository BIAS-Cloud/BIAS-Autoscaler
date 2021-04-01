package com.jaimedantas.enums;

import java.util.HashMap;
import java.util.Map;

public enum ProbabilityConstantC {

    TEM_PERCENT(1.42),
    TWENTY_PERCENT(1.06),
    FIFTY_PERCENT(0.506),
    EIGHTY_PERCENT(0.173);

    private static final Map<Double, ProbabilityConstantC> ALPHA = new HashMap<>();

    static {
        for (ProbabilityConstantC e : values()) {
            ALPHA.put(e.alpha, e);
        }
    }

    public final double alpha;

    private ProbabilityConstantC(double alpha) {
        this.alpha = alpha;

    }

    public static ProbabilityConstantC valueOfLabel(double label) {
        return ALPHA.get(label);
    }
}
