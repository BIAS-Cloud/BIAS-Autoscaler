package com.jaimedantas.autoscaler.scaling;

import com.jaimedantas.configuration.property.ScalingConfiguration;
import com.jaimedantas.enums.ProbabilityConstantC;

import javax.inject.Inject;

public class SquareRootStaffing {

    @Inject
    ScalingConfiguration scalingConfiguration;

    /**
     * Calculates the Square Root Stafding K = R + c * sqrt(R)
     * @param r arrival/mu
     * @return the number of servers k
     */
    public double calculateNumberOfServers(long r) {
        return r + ProbabilityConstantC.valueOf(scalingConfiguration.getProbabilityQueueing()).alpha * Math.sqrt(r);
    }

}
