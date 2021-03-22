package com.jaimedantas.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@ConfigurationProperties("autoscaler")
public class AutoscalerConfiguration {
    String zone;
    String project;
}
