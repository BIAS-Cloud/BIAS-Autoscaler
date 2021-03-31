package com.jaimedantas.service;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.Aggregation;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Timestamps;
import com.jaimedantas.configuration.AutoscalerConfiguration;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonitorLoadBalancer {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(MonitorInstances.class);

    @SneakyThrows
    @Scheduled(fixedDelay = "2m")
    void executeEveryTen() {

        logger.info("Running the scheduler for Load Balancer metrics");

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests.
        try (MetricServiceClient metricServiceClient = MetricServiceClient.create()) {
            ProjectName projectName = ProjectName.of(autoscalerConfiguration.getProject());

            // Restrict time to last 20 minutes
            long startMillis = System.currentTimeMillis() - ((60 * 6) * 1000);
            TimeInterval interval =
                    TimeInterval.newBuilder()
                            .setStartTime(Timestamps.fromMillis(startMillis))
                            .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()-1000))
                            .build();

            Aggregation aggregation =
                    Aggregation.newBuilder()
                            .setAlignmentPeriod(Duration.newBuilder().setSeconds(181).build())
                            .setPerSeriesAligner(Aggregation.Aligner.ALIGN_MEAN)
                            .build();

            // Prepares the list time series request
            ListTimeSeriesRequest request =
                    ListTimeSeriesRequest.newBuilder()
                            .setName(projectName.toString())
                            .setFilter("metric.type = \"loadbalancing.googleapis.com/https/backend_request_count\"")
                            .setInterval(interval)
//                            .setAggregation(aggregation)
                            .build();

            // Send the request to list the time series
            MetricServiceClient.ListTimeSeriesPagedResponse response =
                    metricServiceClient.listTimeSeries(request);

            // Process the response
            response.iterateAll().forEach(
                    timeSeries -> {
                        logger.info("Instance Group: {}", timeSeries.getResource().getLabelsMap().get("backend_name"));
                        timeSeries.getPointsList().forEach(
                                point -> logger.info("Requests: {}", point.getValue().getDoubleValue()));
                    }
            );
        }

    }

}
