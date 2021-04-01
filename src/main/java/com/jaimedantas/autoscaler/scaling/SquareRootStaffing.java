package com.jaimedantas.autoscaler.scaling;

import com.google.api.gax.rpc.InvalidArgumentException;
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
    public double calculateNumberOfServers(long r) throws Exception {

        double c;

        if (scalingConfiguration.getProbabilityQueueing() == 0.1){ //TODO Remove this mapper
            c = ProbabilityConstantC.TEM_PERCENT.alpha;
        } else if(scalingConfiguration.getProbabilityQueueing() == 0.2) {
            c = ProbabilityConstantC.TEM_PERCENT.alpha;
        } else if(scalingConfiguration.getProbabilityQueueing() == 0.5) {
            c = ProbabilityConstantC.FIFTY_PERCENT.alpha;
        } else if(scalingConfiguration.getProbabilityQueueing() == 0.8) {
            c = ProbabilityConstantC.EIGHTY_PERCENT.alpha;
        } else {
            throw new Exception("Value of mu not defined");
        }

        return r + c * Math.sqrt(r);
    }

}
