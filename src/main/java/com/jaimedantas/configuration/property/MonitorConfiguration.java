package com.jaimedantas.configuration.property;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@ConfigurationProperties("monitor")
public class MonitorConfiguration {

    String loadBalancerInterval;
    String cpuInstancesInterval;

}
