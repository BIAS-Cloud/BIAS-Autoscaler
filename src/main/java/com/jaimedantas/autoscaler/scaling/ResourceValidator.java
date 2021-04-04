package com.jaimedantas.autoscaler.scaling;

import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;
import com.jaimedantas.enums.InstanceType;

import javax.inject.Inject;
import java.util.HashMap;

public class ResourceValidator {

    @Inject
    ScalingConfiguration scalingConfiguration;

    public int validateRegularInstances(int regularInstances){
        return Math.min(regularInstances, scalingConfiguration.getMaximumRegularInstances());
    }

    public int validateBurstableInstances(int burstableInstances){
        return Math.min(burstableInstances, scalingConfiguration.getMaximumBurstableIntances());
    }

    /**
     * Validates the number of instances accordingly with the properties defined
     * @param regularInstances
     * @param burstableInstances
     * @return
     */
    public HashMap<InstanceType, Integer> validateResources(int regularInstances, int burstableInstances){

        regularInstances = Math.max(regularInstances, scalingConfiguration.getMinimumRegularInstances());
        burstableInstances = Math.max(burstableInstances, scalingConfiguration.getMinimumBurstableInstances());

        HashMap<InstanceType, Integer> numberOfInstances = new HashMap<>();

        int currentk = regularInstances + burstableInstances;

        while (currentk > scalingConfiguration.getMaximumInstances()) {
            regularInstances = this.decreaseRegularInstances(regularInstances);
            currentk = regularInstances + burstableInstances;
            if (currentk > scalingConfiguration.getMaximumInstances()) {
                burstableInstances = this.decreaseBurstableInstances(burstableInstances);
            }
            currentk = regularInstances + burstableInstances;
        }

        numberOfInstances.put(InstanceType.ONDEMAND, regularInstances);
        numberOfInstances.put(InstanceType.BURSTABLE, burstableInstances);

        return numberOfInstances;
    }

    private int decreaseBurstableInstances(int burstableInstances){
        burstableInstances--;
        return Math.max(burstableInstances, scalingConfiguration.getMinimumBurstableInstances());
    }

    private int decreaseRegularInstances(int regularInstances){
        regularInstances--;
        return Math.max(regularInstances, scalingConfiguration.getMinimumRegularInstances());
    }

}
