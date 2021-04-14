package com.jaimedantas.autoscaler.monitor.command;

import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.model.ArrivalRate;
import com.jaimedantas.model.InstanceCpuUtilization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetricsCommand {

    /**
     * Gets last arrival rate of both groups
     * @param cpuList
     * @return
     */
    public static long getArrivalRequests(List<ArrivalRate> cpuList){
        List<ArrivalRate> ondemandList = new ArrayList<>();
        List<ArrivalRate> burstableList = new ArrayList<>();

        if (Objects.nonNull(cpuList) && !cpuList.isEmpty()) {
            cpuList.forEach(e -> {
                if (Objects.nonNull(e.getInstanceType()) && e.getInstanceType().equals(InstanceType.ONDEMAND)){
                    ondemandList.add(e);
                } else if (Objects.nonNull(e.getInstanceType()) && e.getInstanceType().equals(InstanceType.BURSTABLE)){
                    burstableList.add(e);
                }
            });
        }

        if (!ondemandList.isEmpty()){
            if (!burstableList.isEmpty()){
                return ondemandList.get(0).getValue() + burstableList.get(0).getValue();
            } else {
                return ondemandList.get(0).getValue();
            }
        } else {
            return 0;
        }

    }

    /**
     * Finds and reduces the cpu usage of the burstable instances from monitoring list (gets last entry)
     * @param cpuList
     * @return
     */
    public static double getCpuBurstable(List<InstanceCpuUtilization> cpuList){
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
                    cpuValuesList.add(valuesListAux.get(0));
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

    /**
     * Gets the current number of instances of the cluster
     * @param cpuList
     * @return
     */
    @Deprecated
    public static HashMap<InstanceType, Integer> getCurrentNumberInstances(List<InstanceCpuUtilization> cpuList){
        HashMap<InstanceType, Integer> numberOfInstances = new HashMap<>();
        List<String> instancesNamesBurstable = new ArrayList<>();
        List<String> instancesNamesRegular = new ArrayList<>();
        int burstableInstances = 0;
        int regularInstances = 0;

        //gets names of the instances
        cpuList.forEach( e -> {
            if (e.getInstanceName().contains(InstanceType.BURSTABLE.label.toLowerCase())){
                instancesNamesBurstable.add(e.getInstanceName());
            } else if (e.getInstanceName().contains(InstanceType.ONDEMAND.label.toLowerCase())){
                instancesNamesRegular.add(e.getInstanceName());
            }
        });
        List<String> instancesNamesBNoDuplicated = instancesNamesBurstable.stream()
                .distinct()
                .collect(Collectors.toList());

        List<String> instancesNamesRNoDuplicated = instancesNamesRegular.stream()
                .distinct()
                .collect(Collectors.toList());

        if (!instancesNamesBNoDuplicated.isEmpty()) {
            burstableInstances = (int) instancesNamesBNoDuplicated.stream().filter(e -> e.contains(InstanceType.BURSTABLE.label.toLowerCase())).count();
        }
        if (!instancesNamesRNoDuplicated.isEmpty()) {
            regularInstances = (int) instancesNamesRNoDuplicated.stream().filter(e -> e.contains(InstanceType.ONDEMAND.label.toLowerCase())).count();
        }

        numberOfInstances.put(InstanceType.BURSTABLE, burstableInstances);
        numberOfInstances.put(InstanceType.ONDEMAND, regularInstances);

        return numberOfInstances;
    }


    /**
     * Finds and reduces the cpu usage of the regular ondemand instances from monitoring list (gets last entry)
     * @param cpuList
     * @return
     */
    public static double getCpuOndemand(List<InstanceCpuUtilization> cpuList){
        List<Double> cpuValuesList = new ArrayList<>();
        List<String> instancesNames = new ArrayList<>();
        if (!cpuList.isEmpty()) {

            //gets names of the instances
            cpuList.forEach( e -> {
                if (e.getInstanceName().contains(InstanceType.ONDEMAND.label.toLowerCase())){
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
                    cpuValuesList.add(valuesListAux.get(0));
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
