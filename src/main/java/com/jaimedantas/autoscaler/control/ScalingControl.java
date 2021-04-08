package com.jaimedantas.autoscaler.control;

import com.jaimedantas.autoscaler.Orchestrator;
import com.jaimedantas.autoscaler.scaling.state.ScalingState;
import com.jaimedantas.configuration.autoscaler.AutoscalerConfiguration;
import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;
import com.jaimedantas.enums.InstanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ScalingControl {

    private static final Logger logger = LoggerFactory.getLogger(Orchestrator.class);

    @Inject
    ScalingConfiguration scalingConfiguration;

    @Inject
    InstanceAllocation instanceAllocation;

    @Inject
    BackendServiceUsage backendServiceUsage;

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    public void performScaling(int regularInstances, int currentRegularInstances, int burstableInstances, int currentBurstableInstances) throws IOException, GeneralSecurityException {

        // weight of burstable
        if (regularInstances > currentRegularInstances) {
            if (ScalingState.getCurrentBurstableWeight() != 1.0) {
                backendServiceUsage.setBackendServicePolicy(autoscalerConfiguration.getInstanceGroupBurstable(), autoscalerConfiguration.getBackendService(), 1.0f);
            }
        } else {
            if (ScalingState.getCurrentBurstableWeight() != scalingConfiguration.getCpuUtilizationBurstable().floatValue()) {
                backendServiceUsage.setBackendServicePolicy(autoscalerConfiguration.getInstanceGroupBurstable(), autoscalerConfiguration.getBackendService(), scalingConfiguration.getCpuUtilizationBurstable().floatValue());
            }
        }

        if (regularInstances > currentRegularInstances){
            if (ScalingState.getLastScaleOutRegular() == 0){
                //first time scaling
                logger.warn("Scaling out REGULAR instance");
                ScalingState.setLastScaleOutRegular(System.currentTimeMillis());
                instanceAllocation.addInstanceToGroup(InstanceType.ONDEMAND);
            } else {
                if(System.currentTimeMillis() > ScalingState.getLastScaleOutRegular() + scalingConfiguration.getAutoscalerScaleWaitingTime()*1000){
                    logger.warn("Scaling out REGULAR instance");
                    ScalingState.setLastScaleOutRegular(System.currentTimeMillis());
                    instanceAllocation.addInstanceToGroup(InstanceType.ONDEMAND);
                } else {
                    logger.info("Waiting the index of new REGULAR instance");
                }
            }
        } else if (regularInstances < currentRegularInstances){
            if (ScalingState.getLastScaleInRegular() == 0) {
                logger.warn("Scaling in REGULAR instance");
                ScalingState.setLastScaleInRegular(System.currentTimeMillis());
                instanceAllocation.removeInstanceFromGroup(InstanceType.ONDEMAND);
            } else {
                if(System.currentTimeMillis() > ScalingState.getLastScaleInRegular() + scalingConfiguration.getAutoscalerScaleWaitingTime()*1000) {
                    logger.warn("Scaling in REGULAR instance");
                    ScalingState.setLastScaleInRegular(System.currentTimeMillis());
                    instanceAllocation.removeInstanceFromGroup(InstanceType.ONDEMAND);
                }
                else {
                    logger.info("Waiting the removal of old REGULAR instance");
                }
            }
        }
        if (burstableInstances > currentBurstableInstances){
            if (ScalingState.getLastScaleOutBurstable() == 0) {
                logger.warn("Scaling out BURSTABLE instance");
                ScalingState.setLastScaleOutBurstable(System.currentTimeMillis());
                instanceAllocation.addInstanceToGroup(InstanceType.BURSTABLE);
            } else {
                if(System.currentTimeMillis() > ScalingState.getLastScaleOutBurstable() + scalingConfiguration.getAutoscalerScaleWaitingTime()*1000) {
                    logger.warn("Scaling out BURSTABLE instance");
                    ScalingState.setLastScaleOutBurstable(System.currentTimeMillis());
                    instanceAllocation.addInstanceToGroup(InstanceType.BURSTABLE);
                }
                else {
                    logger.info("Waiting the index of new BURSTABLE instance");
                }
            }
        } else if (burstableInstances < currentBurstableInstances){
            if (ScalingState.getLastScaleInBurstable() == 0) {
                logger.warn("Scaling in BURSTABLE instance");
                ScalingState.setLastScaleInBurstable(System.currentTimeMillis());
                instanceAllocation.removeInstanceFromGroup(InstanceType.BURSTABLE);
            } else {
                if(System.currentTimeMillis() > ScalingState.getLastScaleInBurstable() + scalingConfiguration.getAutoscalerScaleWaitingTime()*1000) {
                    logger.warn("Scaling in BURSTABLE instance");
                    ScalingState.setLastScaleInBurstable(System.currentTimeMillis());
                    instanceAllocation.removeInstanceFromGroup(InstanceType.BURSTABLE);
                }
                else {
                    logger.info("Waiting the removal of old REGULAR instance");
                }
            }
        }
    }
}
