package com.jaimedantas.configuration.autoscaler;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties("scaling")
public class ScalingConfiguration {

    String scaling;
    int autoscalerScaleWaitingTime;
    int maximumRegularInstances;
    int currentRegularInstances;
    int currentBurstableInstances;
    int maximumBurstableIntances;
    int minimumBurstableInstances;
    int minimumRegularInstances;
    int maximumInstances;
    Double cpuUtilizationBurstable;
    Double probabilityQueueing;
    String requestsSamples;
    String cpuSamples;
    String autoscalerControlInterval;
    Double mu;
    Double m;

}
