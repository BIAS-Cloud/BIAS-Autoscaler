package com.jaimedantas.autoscaler.scaling;

import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;
import com.jaimedantas.enums.ProbabilityConstantC;
import com.jaimedantas.exception.InvalidProbabilityQueueException;

import javax.inject.Inject;

public class SquareRootStaffing {

    @Inject
    ScalingConfiguration scalingConfiguration;

    /**
     * Calculates the Square Root Staffing k = R + c * sqrt(R)
     * @param r arrival/mu
     * @return the number of instances k
     */
    private int calculateNumberOfInstances(long r) throws InvalidProbabilityQueueException {
        return (int) Math.round(r + getProbabilityQueue() * Math.sqrt(r));
    }

    /**
     * Calculates the Square Root Staffing with overprovisioning k_m = m * R + c * sqrt(m * R)
     * @param r arrival/mu
     * @return the number of instances k_m
     */
    private int calculateNumberOfInstancesWithOverprovisioning(long r) throws InvalidProbabilityQueueException {
        return (int) Math.round(((scalingConfiguration.getM() - 1)  * r) + getProbabilityQueue() * Math.sqrt(scalingConfiguration.getM() * r));
    }

    /**
     * Calculates the Square Root Stafding k = R + c * sqrt(R)
     * The regular instances is R
     * @param r arrival/mu
     * @return the number of regular instances RD
     */
    public int calculateNumberOfRegularInstances(long r){
        return (int) r;
    }

    /**
     * Calculates the Square Root Stafding k = R + c * sqrt(R)
     * The burstable instances is c * sqrt(R)
     * @param r arrival/mu
     * @return the number of burstable instances (k - R)
     */
    public int calculateNumberOfBurstableInstances(long r) throws InvalidProbabilityQueueException {
        if (scalingConfiguration.getM() > 1.0){
            return calculateNumberOfInstancesWithOverprovisioning(r) - (int) r;
        } else {
            return this.calculateNumberOfInstances(r) - (int) r;
        }
    }

    /**
     * Mapper for the probability of queue alpha (c)
     * @return
     * @throws InvalidProbabilityQueueException
     */
    private double getProbabilityQueue() throws InvalidProbabilityQueueException {
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
            throw new InvalidProbabilityQueueException();
        }
        return c;
    }


}
