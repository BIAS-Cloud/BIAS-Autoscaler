package com.jaimedantas.autoscaler;

import com.jaimedantas.autoscaler.monitor.MonitorInstances;
import com.jaimedantas.autoscaler.scaling.Resource;
import com.jaimedantas.autoscaler.scaling.SquareRootStaffing;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Autoscaler {

    private static final Logger logger = LoggerFactory.getLogger(Autoscaler.class);

    @Inject
    SquareRootStaffing squareRootStaffing;

    @Inject
    Resource resource;

    @SneakyThrows
    @Scheduled(fixedDelay = "${scaling.autoscaler-control-interval}")
    void executeEveryTen() {

        logger.info("Running the scheduler for Autoscaler");

        int r = resource.calculateR(100);

        squareRootStaffing.calculateNumberOfServers(r);

    }
}
