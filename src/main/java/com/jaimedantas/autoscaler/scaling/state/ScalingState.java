package com.jaimedantas.autoscaler.scaling.state;


public class ScalingState {
    public static long lastScaleInBurstable;
    public static long lastScaleOutBurstable;
    public static long lastScaleInRegular;
    public static long lastScaleOutRegular;
    public static double currentBurstableWeight;

    public ScalingState() {
        lastScaleInBurstable = 0;
        lastScaleOutBurstable = 0;
        lastScaleInRegular = 0;
        lastScaleOutRegular = 0;
        currentBurstableWeight = 0.0;
    }

    public static double getCurrentBurstableWeight() {
        return currentBurstableWeight;
    }

    public static void setCurrentBurstableWeight(double currentBurstableWeight) {
        ScalingState.currentBurstableWeight = currentBurstableWeight;
    }

    public static long getLastScaleInBurstable() {
        return lastScaleInBurstable;
    }

    public static void setLastScaleInBurstable(long lastScaleInBurstable) {
        ScalingState.lastScaleInBurstable = lastScaleInBurstable;
    }

    public static long getLastScaleOutBurstable() {
        return lastScaleOutBurstable;
    }

    public static void setLastScaleOutBurstable(long lastScaleOutBurstable) {
        ScalingState.lastScaleOutBurstable = lastScaleOutBurstable;
    }

    public static long getLastScaleInRegular() {
        return lastScaleInRegular;
    }

    public static void setLastScaleInRegular(long lastScaleInRegular) {
        ScalingState.lastScaleInRegular = lastScaleInRegular;
    }

    public static long getLastScaleOutRegular() {
        return lastScaleOutRegular;
    }

    public static void setLastScaleOutRegular(long lastScaleOutRegular) {
        ScalingState.lastScaleOutRegular = lastScaleOutRegular;
    }
}
