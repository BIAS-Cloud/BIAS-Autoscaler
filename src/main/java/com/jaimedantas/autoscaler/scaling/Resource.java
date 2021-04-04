package com.jaimedantas.autoscaler.scaling;

import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;

import javax.inject.Inject;

public class Resource {

    @Inject
    ScalingConfiguration scalingConfiguration;

    /**
     * Calculates the resource required R = arrival/mu
     * @param arrivalRate
     * @return
     */
    public int calculateR(long arrivalRate){
        return (int) Math.ceil(arrivalRate/scalingConfiguration.getMu());
    }
}
