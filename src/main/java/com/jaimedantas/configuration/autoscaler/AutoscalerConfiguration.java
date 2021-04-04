package com.jaimedantas.configuration.autoscaler;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@ConfigurationProperties("autoscaler")
public class AutoscalerConfiguration {

    String zone;
    String region;
    String project;
    String machineTypeOndemand;
    String machineTypeBurstable;
    String instanceGroupOndemand;
    String monitoringGroupOndemand;
    String instanceGroupBurstable;
    String monitoringGroupBurstable;
    String machineImageOndemand;
    String machineImageBurstable;

}
