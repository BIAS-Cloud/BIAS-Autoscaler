package com.jaimedantas.configuration.property;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties("scaling")
public class ScalingConfiguration {

    String scaling;
    String maximumRegularInstances;
    String maximumBurstableIntances;
    String maximumInstances;
    String cpuUtilizationBurstable;
    Double probabilityQueueing;
    String requestsSamples;
    String cpuSamples;
    String autoscalerControlInterval;
    Double mu;

}
