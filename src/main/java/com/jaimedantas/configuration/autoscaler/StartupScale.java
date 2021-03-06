package com.jaimedantas.configuration.autoscaler;

import com.jaimedantas.autoscaler.Orchestrator;
import com.jaimedantas.autoscaler.control.InstanceAllocation;
import com.jaimedantas.autoscaler.control.ScalingControl;
import com.jaimedantas.autoscaler.monitor.MonitorInstances;
import com.jaimedantas.autoscaler.scaling.SquareRootStaffing;
import com.jaimedantas.autoscaler.scaling.state.ScalingState;
import com.jaimedantas.enums.InstanceType;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;

@Context
@Requires(env = "prod")
public class StartupScale {

    private static final Logger logger = LoggerFactory.getLogger(Orchestrator.class);

    @Inject
    SquareRootStaffing squareRootStaffing;

    @Inject
    MonitorInstances monitorInstances;

    @Inject
    ScalingConfiguration scalingConfiguration;

    @Inject
    ScalingControl scalingControl;

    @Inject
    InstanceAllocation instanceAllocation;

    @PostConstruct
    void StartupScale() throws IOException, GeneralSecurityException {

        logger.warn("Running startup check on cluster");

        int currentRegularInstances = monitorInstances.getNumberOfInstances(InstanceType.ONDEMAND);
        int currentBurstableInstances = monitorInstances.getNumberOfInstances(InstanceType.BURSTABLE);

        if (currentBurstableInstances < scalingConfiguration.getMinimumBurstableInstances()){
            logger.warn("Scaling out BURSTABLE instance");
            ScalingState.setLastScaleOutBurstable(System.currentTimeMillis());
            instanceAllocation.addInstanceToGroup(InstanceType.BURSTABLE);
        }
        if (currentRegularInstances < scalingConfiguration.getMinimumRegularInstances()){
            logger.warn("Scaling out REGULAR instance");
            ScalingState.setLastScaleOutRegular(System.currentTimeMillis());
            instanceAllocation.addInstanceToGroup(InstanceType.ONDEMAND);
        }

        logger.warn("Creating CSV file");
        try (PrintWriter writer = new PrintWriter(new File("history.csv"))) {

            String sb = "timestamp,cpu_burstable,cpu_regular,weight,arrival_rate,predicted_burstable,predicted_regular,current_burstable,current_regular\n";
            writer.write(sb);

        } catch (Exception e) {
            logger.error("Could not create CSV file", e);
        }

    }
}
