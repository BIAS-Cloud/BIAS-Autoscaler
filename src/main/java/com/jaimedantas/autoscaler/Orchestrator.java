package com.jaimedantas.autoscaler;

import com.jaimedantas.autoscaler.monitor.MonitorInstances;
import com.jaimedantas.autoscaler.monitor.MonitorLoadBalancer;
import com.jaimedantas.autoscaler.scaling.Resource;
import com.jaimedantas.autoscaler.scaling.SquareRootStaffing;
import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.model.ArrivalRate;
import com.jaimedantas.model.InstanceCpuUtilization;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class Orchestrator {

    private static final Logger logger = LoggerFactory.getLogger(Orchestrator.class);

    @Inject
    SquareRootStaffing squareRootStaffing;

    @Inject
    MonitorLoadBalancer monitorLoadBalancer;

    @Inject
    MonitorInstances monitorInstances;

    @Inject
    Resource resource;

    @SneakyThrows
    @Scheduled(fixedDelay = "${scaling.autoscaler-decision-interval}")
    void executeAutoscaler() {

        logger.info("Running the scheduler for the Autoscaler");

        List<ArrivalRate> arrivalRateList = monitorLoadBalancer.fetchArrivalRate();

        List<InstanceCpuUtilization> instanceCpuUtilizationList = monitorInstances.fetchCpuInstances();

        int r = resource.calculateR(100);

        long lastRequest = getArrivalOndemand(arrivalRateList);

        logger.info("Arrival Rate Ondemand: {} req/s", lastRequest);

        double cpuBurstable = getCpuOndemand(instanceCpuUtilizationList);

        logger.info("CPU of Burstable: {} %", cpuBurstable*100);


        squareRootStaffing.calculateNumberOfServers(r);

    }

    long getArrivalOndemand(List<ArrivalRate> cpuList){
        List<ArrivalRate> ondemandList = new ArrayList<>();
        if (cpuList != null) {
            cpuList.forEach(e -> {
                if (e.getInstanceType().equals(InstanceType.ONDEMAND)){
                    ondemandList.add(e);
                }
            });
        }

        if (!ondemandList.isEmpty()){
            return ondemandList.get(0).getValue();
        } else{
            return 0;
        }

    }

    double getCpuOndemand(List<InstanceCpuUtilization> cpuList){
        List<Double> cpuValuesList = new ArrayList<>();
        List<String> instancesNames = new ArrayList<>();
        if (!cpuList.isEmpty()) {

            //gets names of the instances
            cpuList.forEach( e -> {
                if (e.getInstanceName().contains(InstanceType.BURSTABLE.label.toLowerCase())){
                    instancesNames.add(e.getInstanceName());
                }
            });
            List<String> instancesNamesNoDuplicated = instancesNames.stream()
                    .distinct()
                    .collect(Collectors.toList());

            //gets the last cpu value
            if (!instancesNamesNoDuplicated.isEmpty()){
                instancesNamesNoDuplicated.forEach( n -> {
                    List<Double> valuesListAux = new ArrayList<>();
                    cpuList.forEach( i -> {
                        if (i.getInstanceName().equals(n)){
                            valuesListAux.add(i.getValue());
                        }
                    });
                    cpuValuesList.add(valuesListAux.get(valuesListAux.size()-1));
                });
            }

            return cpuValuesList.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
        } else {
            return 0.0;
        }
    }
}











