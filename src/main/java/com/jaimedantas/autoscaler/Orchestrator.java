package com.jaimedantas.autoscaler;

import com.jaimedantas.autoscaler.control.BackendServiceUsage;
import com.jaimedantas.autoscaler.control.ScalingControl;
import com.jaimedantas.autoscaler.monitor.MonitorInstances;
import com.jaimedantas.autoscaler.monitor.MonitorLoadBalancer;
import com.jaimedantas.autoscaler.monitor.command.MetricsCommand;
import com.jaimedantas.autoscaler.scaling.Resource;
import com.jaimedantas.autoscaler.scaling.ResourceValidator;
import com.jaimedantas.autoscaler.scaling.SquareRootStaffing;
import com.jaimedantas.autoscaler.scaling.state.ScalingState;
import com.jaimedantas.configuration.autoscaler.AutoscalerConfiguration;
import com.jaimedantas.configuration.autoscaler.ScalingConfiguration;
import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.exception.InvalidProbabilityQueueException;
import com.jaimedantas.model.ArrivalRate;
import com.jaimedantas.model.InstanceCpuUtilization;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
    AutoscalerConfiguration autoscalerConfiguration;

    @Inject
    MonitorLoadBalancer monitorLoadBalancer;

    @Inject
    MonitorInstances monitorInstances;

    @Inject
    ResourceValidator resourceValidator;

    @Inject
    BackendServiceUsage backendServiceUsage;

    @Inject
    ScalingControl scalingControl;

    @Inject
    Resource resource;

    @Scheduled(fixedDelay = "${scaling.autoscaler-decision-interval}", initialDelay = "10s")
    void executeAutoscaler() throws IOException, GeneralSecurityException, InvalidProbabilityQueueException {

        logger.info("Running the scheduler for the Autoscaler");

        // monitoring
        List<ArrivalRate> arrivalRateList = monitorLoadBalancer.fetchArrivalRate();
        List<InstanceCpuUtilization> instanceCpuUtilizationList = monitorInstances.fetchCpuInstances();
        long lastArrivalRate = MetricsCommand.getArrivalOndemand(arrivalRateList);
        double cpuBurstable = MetricsCommand.getCpuBurstable(instanceCpuUtilizationList);
        double cpuOndemand = MetricsCommand.getCpuOndemand(instanceCpuUtilizationList);
        // weight of burstable
        ScalingState.setCurrentBurstableWeight(backendServiceUsage.getBurstableWeight());

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
        int currentRegularInstances = Math.max(scalingConfiguration.getCurrentRegularInstances(), monitorInstances.getNumberOfInstances(InstanceType.ONDEMAND));
        int currentBurstableInstances = Math.max(scalingConfiguration.getCurrentBurstableInstances(), monitorInstances.getNumberOfInstances(InstanceType.BURSTABLE));

        logger.info("Current REGULAR = {}", currentRegularInstances);
        logger.info("Current BURSTABLE = {}", currentBurstableInstances);

        scalingControl.performScaling(regularInstances, currentRegularInstances, burstableInstances, currentBurstableInstances);

    }

}











