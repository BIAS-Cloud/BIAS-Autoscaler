package com.jaimedantas.autoscaler.monitor;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.protobuf.util.Timestamps;
import com.jaimedantas.configuration.property.AutoscalerConfiguration;
import com.jaimedantas.enums.InstanceType;
import com.jaimedantas.model.ArrivalRate;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MonitorLoadBalancer {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(MonitorLoadBalancer.class);

    @SneakyThrows
    public List<ArrivalRate> fetchArrivalRate() {

        logger.info("Getting Load Balancer metrics");

        List<ArrivalRate> arrivalRates = new ArrayList<>();

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
                            .setFilter("metric.type = \"loadbalancing.googleapis.com/https/backend_request_count\"")
                            .setInterval(interval)
                            .build();

            // Send the request to list the time series
            MetricServiceClient.ListTimeSeriesPagedResponse response =
                    metricServiceClient.listTimeSeries(request);

            // Process the response
            response.iterateAll().forEach(
                    timeSeries -> {
                        String backendName = timeSeries.getResource().getLabelsMap().get("backend_name");
                        logger.info("Instance Group: {}", backendName);
                        InstanceType instanceType = null;
                        if(backendName.contains(InstanceType.ONDEMAND.label.toLowerCase())){
                            instanceType = InstanceType.ONDEMAND;
                        } else if (backendName.contains(InstanceType.BURSTABLE.label.toLowerCase())){
                            instanceType = InstanceType.BURSTABLE;
                        }
                        InstanceType finalInstanceType = instanceType;
                        timeSeries.getPointsList().forEach(
                                point -> {
                                    logger.info("Requests: {}", point.getValue().getInt64Value());
                                    ArrivalRate arrivalRate = new ArrivalRate();
                                    arrivalRate.setInstanceType(finalInstanceType);
                                    arrivalRate.setValue(point.getValue().getInt64Value());
                                    arrivalRate.setTimestamp(point.getInterval().getEndTime());

                                    arrivalRates.add(arrivalRate);
                                });
                    }
            );
        }
        return arrivalRates;

    }

}
