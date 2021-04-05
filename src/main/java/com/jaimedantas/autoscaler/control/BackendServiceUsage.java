package com.jaimedantas.autoscaler.control;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.Operation;
import com.jaimedantas.autoscaler.Orchestrator;
import com.jaimedantas.configuration.authentication.GoogleCloudAuth;
import com.jaimedantas.configuration.autoscaler.AutoscalerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

public class BackendServiceUsage {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(Orchestrator.class);

    /**
     * Calls the BackendService from GCP to get its info. The backend service is used for the policy
     * of load balancing.
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public BackendService getBackendServiceInfo(String backendService) throws IOException, GeneralSecurityException {

        Compute.BackendServices.Get request =
                GoogleCloudAuth.computeService().backendServices().get(autoscalerConfiguration.getProject(), backendService);

        return request.execute();
    }

    /**
     * Calls the BackendService from GCP to get the current weight of the burstable instances.
     * of load balancing.
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public double getBurstableWeight() throws IOException, GeneralSecurityException {

        Compute.BackendServices.Get request =
                GoogleCloudAuth.computeService().backendServices().get(autoscalerConfiguration.getProject(), autoscalerConfiguration.getBackendService());
        BackendService backendServiceInformation = request.execute();

        AtomicReference<Float> maxUtilization = new AtomicReference<>();
        backendServiceInformation.getBackends().forEach(backend -> {
            if (backend.getGroup().contains(autoscalerConfiguration.getInstanceGroupBurstable())){
                maxUtilization.set(backend.getMaxUtilization());
            }
        });

        return maxUtilization.get();
    }

    /**
     * Sets the load balancing policy for an instance group of a service.
     * @param instanceGroup
     * @param backendService
     * @param cpuUtilization 0 to 1
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void setBackendServicePolicy(String instanceGroup, String backendService, float cpuUtilization) throws IOException, GeneralSecurityException {

        logger.warn("Updating CPU utilization of IG {} to {}", instanceGroup, cpuUtilization);

        Compute.BackendServices.Get requestWithGetInfo =
                GoogleCloudAuth.computeService().backendServices().get(autoscalerConfiguration.getProject(), backendService);
        BackendService backendServiceInformation = requestWithGetInfo.execute();

        backendServiceInformation.getBackends().forEach(backend -> {
            if (backend.getGroup().contains(instanceGroup)){
                backend.setMaxUtilization(cpuUtilization);
            }
        });

        Compute.BackendServices.Update request =
                GoogleCloudAuth.computeService().backendServices().update(autoscalerConfiguration.getProject(), backendService, backendServiceInformation);

        Operation response = request.execute();

    }

}
