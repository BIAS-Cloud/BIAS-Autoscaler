package com.jaimedantas.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Value;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@ConfigurationProperties("autoscaler")
public class AutoscalerConfiguration {

    String zone;
    String project;
    String machineTypeOndemand;
    String machineTypeBurstable;
    String instanceGroupOndemand;
    String instanceGroupBurstable;
    String machineImageOndemand;
    String machineImageBurstable;

}
