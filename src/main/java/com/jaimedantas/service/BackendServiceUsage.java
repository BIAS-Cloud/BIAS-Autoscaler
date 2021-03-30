package com.jaimedantas.service;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
import com.jaimedantas.configuration.AutoscalerConfiguration;
import com.jaimedantas.configuration.GoogleCloudAuth;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class BackendServiceUsage {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

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
     * Sets the load balancing policy for an instance group of a service.
     * @param instanceGroup
     * @param backendService
     * @param cpuUtilization 0 to 1
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void setBackendServicePolicy(String instanceGroup, String backendService, float cpuUtilization) throws IOException, GeneralSecurityException {


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
