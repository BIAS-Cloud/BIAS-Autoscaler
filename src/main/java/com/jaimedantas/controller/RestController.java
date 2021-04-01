package com.jaimedantas.controller;

import com.google.api.services.compute.model.BackendService;
import com.jaimedantas.model.InstanceScale;
import com.jaimedantas.autoscaler.control.BackendServiceUsage;
import com.jaimedantas.autoscaler.control.InstanceAllocation;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller("/autoscaler/v1/")
@Validated
public class RestController {

    @Inject
    BackendServiceUsage backendServiceUsage;

    @Inject
    InstanceAllocation instanceAllocation;

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    /**
     * Retrieves the information about a backend service balancing policy
     * @param backendService Backend Service
     * @return All the information about the backend service
     * @throws IOException
     */
    @SneakyThrows
    @Get(uri = "/service", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<BackendService> getBackendService(@NotBlank String backendService) throws IOException {

        logger.info("Getting info about service: "+backendService);

        return HttpResponse.status(HttpStatus.OK).body(backendServiceUsage.getBackendServiceInfo(backendService));
    }

    /**
     * Sets the load balancing policy for a instance group of a service.
     * @param backendService
     * @param instanceGroup
     * @param cpuUtilization from 0 to 1
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @Post(uri = "/service/policy")
    public void setBackendServicePolicy(@NotBlank String backendService,
                                        @NotBlank String instanceGroup,
                                        @NotBlank float cpuUtilization) throws IOException, GeneralSecurityException {

        logger.info("Setting service policy "+backendService);

        backendServiceUsage.setBackendServicePolicy(instanceGroup, backendService,cpuUtilization);

    }

    /**
     * Creates a new instance and adds it to a instance group
     * @param instanceScale ondemand or busrtable
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @Post(uri = "/instance")
    public void addInstance(@NotBlank InstanceScale instanceScale) throws IOException, GeneralSecurityException {

        logger.info("Adding instance to "+instanceScale.getInstanceType());

        instanceAllocation.addInstanceToGroup(instanceScale.getInstanceType());

    }

    /**
     * Deletes an instance
     * @param instanceScale ondemand or busrtable
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @Delete(uri = "/instance")
    public void deleteInstance(@NotBlank InstanceScale instanceScale) throws IOException, GeneralSecurityException {

        logger.info("Deleting instance from "+instanceScale.getInstanceType());

        instanceAllocation.removeInstanceFromGroup(instanceScale.getInstanceType());

    }
}
