package com.jaimedantas.autoscaler;

import com.jaimedantas.autoscaler.control.BackendServiceUsage;
import com.jaimedantas.autoscaler.control.InstanceAllocation;
import com.jaimedantas.autoscaler.monitor.MonitorInstances;
import com.jaimedantas.autoscaler.monitor.MonitorLoadBalancer;
import com.jaimedantas.autoscaler.monitor.command.MetricsCommand;
import com.jaimedantas.autoscaler.scaling.Resource;
import com.jaimedantas.autoscaler.scaling.ResourceValidator;
import com.jaimedantas.autoscaler.scaling.SquareRootStaffing;
import com.jaimedantas.autoscaler.scaling.state.ScalingState;
import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;
import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.model.ArrivalRate;
import com.jaimedantas.model.InstanceCpuUtilization;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;

@Singleton
public class Orchestrator {

    private static final Logger logger = LoggerFactory.getLogger(Orchestrator.class);

    @Inject
    SquareRootStaffing squareRootStaffing;


    @Inject
    ScalingConfiguration scalingConfiguration;

    @Inject
    MonitorLoadBalancer monitorLoadBalancer;

    @Inject
    MonitorInstances monitorInstances;

    @Inject
    ResourceValidator resourceValidator;

    @Inject
    BackendServiceUsage backendServiceUsage;

    @Inject
    InstanceAllocation instanceAllocation;

    @Inject
    Resource resource;

    @SneakyThrows
    @Scheduled(fixedDelay = "${scaling.autoscaler-decision-interval}")
    void executeAutoscaler() {

        logger.info("Running the scheduler for the Autoscaler");


        // monitoring
        List<ArrivalRate> arrivalRateList = monitorLoadBalancer.fetchArrivalRate();
        List<InstanceCpuUtilization> instanceCpuUtilizationList = monitorInstances.fetchCpuInstances();
        long lastArrivalRate = MetricsCommand.getArrivalOndemand(arrivalRateList);
        double cpuBurstable = MetricsCommand.getCpuBurstable(instanceCpuUtilizationList);
        double cpuOndemand = MetricsCommand.getCpuOndemand(instanceCpuUtilizationList);

        logger.info("Arrival Rate Ondemand: {} req/min", lastArrivalRate);
        logger.info("CPU of Burstable: {}%", String.format("%.2f",cpuBurstable * 100));
        logger.info("CPU of Ondemand: {}%",  String.format("%.2f",cpuOndemand * 100));

        // validation
        int r = resource.calculateR(lastArrivalRate);
        int regularInstances = resourceValidator.validateRegularInstances(squareRootStaffing.calculateNumberOfRegularInstances(r));
        int burstableInstances = resourceValidator.validateBurstableInstances(squareRootStaffing.calculateNumberOfBurstableInstances(r));
        HashMap<InstanceType, Integer> NumberOfInstanceshashMap = resourceValidator.validateResources(regularInstances, burstableInstances);
        regularInstances = NumberOfInstanceshashMap.get(InstanceType.ONDEMAND);
        burstableInstances = NumberOfInstanceshashMap.get(InstanceType.BURSTABLE);

        logger.info("Predicted REGULAR = {}", regularInstances);
        logger.info("Predicted BURSTABLE = {}", burstableInstances);

        // scaling
        //ScalingState scalingState = new ScalingState();
        HashMap<InstanceType, Integer> currentInstances = MetricsCommand.getCurrentNumberInstances(instanceCpuUtilizationList);
        int currentRegularInstances = Math.max(scalingConfiguration.getCurrentRegularInstances(), currentInstances.get(InstanceType.ONDEMAND));
        int currentBurstableInstances = Math.max(scalingConfiguration.getCurrentBurstableInstances(), currentInstances.get(InstanceType.BURSTABLE));

        logger.info("Current REGULAR = {}", currentRegularInstances);
        logger.info("Current BURSTABLE = {}", currentBurstableInstances);

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











