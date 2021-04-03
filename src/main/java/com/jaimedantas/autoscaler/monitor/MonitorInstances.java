package com.jaimedantas.autoscaler.monitor;


import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.protobuf.util.Timestamps;
import com.jaimedantas.configuration.property.AutoscalerConfiguration;
import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.model.InstanceCpuUtilization;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


@Singleton
public class MonitorInstances {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(MonitorInstances.class);

    @SneakyThrows
    public List<InstanceCpuUtilization> fetchCpuInstances() {

        logger.info("Getting for CPU metrics");

        List<InstanceCpuUtilization> instanceCpuUtilizationList = new ArrayList<>();

        try (MetricServiceClient metricServiceClient = MetricServiceClient.create()) {
            ProjectName projectName = ProjectName.of(autoscalerConfiguration.getProject());

            // Restrict time to last 6 minutes
            long startMillis = System.currentTimeMillis() - ((60 * 6) * 1000);
            TimeInterval interval =
                    TimeInterval.newBuilder()
                            .setStartTime(Timestamps.fromMillis(startMillis))
                            .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()-1000))
                            .build();

            // Prepares the list time series request
            ListTimeSeriesRequest request =
                    ListTimeSeriesRequest.newBuilder()
                            .setName(projectName.toString())
                            .setFilter("metric.type = \"compute.googleapis.com/instance/cpu/utilization\"")
                            .setInterval(interval)
                            .build();

            // Send the request to list the time series
            MetricServiceClient.ListTimeSeriesPagedResponse response =
                    metricServiceClient.listTimeSeries(request);

            // Process the response
            response.iterateAll().forEach(
                    timeSeries -> {
                        String instanceName = timeSeries.getMetric().getLabelsMap().get("instance_name");
                        logger.info("Instance name: {}", instanceName);
                        InstanceType instanceType = null;
                        if(instanceName.contains(InstanceType.ONDEMAND.label.toLowerCase())){
                            instanceType = InstanceType.ONDEMAND;
                        } else if (instanceName.contains(InstanceType.BURSTABLE.label.toLowerCase())){
                            instanceType = InstanceType.BURSTABLE;
                        }
                        InstanceType finalInstanceType = instanceType;
                        timeSeries.getPointsList().forEach(
                                point -> {
                                    logger.info("CPU: {}%", point.getValue().getDoubleValue()*100);
                                    InstanceCpuUtilization instanceCpuUtilization = new InstanceCpuUtilization();
                                    instanceCpuUtilization.setInstanceName(instanceName);
                                    instanceCpuUtilization.setInstanceType(finalInstanceType);
                                    instanceCpuUtilization.setValue(point.getValue().getDoubleValue());
                                    instanceCpuUtilization.setTimestamp(point.getInterval().getEndTime());

                                    instanceCpuUtilizationList.add(instanceCpuUtilization);
                                });
                    }
            );
        }
        return instanceCpuUtilizationList;

    }

}
