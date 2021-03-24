package com.jaimedantas.service;


import com.google.api.Metric;
import com.google.api.MonitoredResource;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.*;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Timestamps;
import com.jaimedantas.configuration.AutoscalerConfiguration;
import com.jaimedantas.enums.InstanceType;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class MonitorInstances {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(MonitorInstances.class);

    @SneakyThrows
    @Scheduled(fixedDelay = "1s")
    void executeEveryTen() {


                logger.info("CPU of burstable: {}%", 1);

    }

}
