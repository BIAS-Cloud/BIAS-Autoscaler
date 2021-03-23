package com.jaimedantas.service;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
import com.jaimedantas.configuration.AutoscalerConfiguration;
import com.jaimedantas.configuration.GoogleCloudAuth;
import com.jaimedantas.enums.InstanceType;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BackendUsageService {

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

    /**
     * Adds a new instances to a existing security group
     * @param instanceType ondemand or busrtable
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void addInstanceToGroup(InstanceType instanceType) throws IOException, GeneralSecurityException {
        Instance requestBodyI = new Instance();
        String instanceGroup = null;
        UUID uuid = UUID.randomUUID();

        if (InstanceType.BURSTABLE.equals(instanceType)) {
            requestBodyI.setName(InstanceType.BURSTABLE.toString().toLowerCase()+"-"+uuid);

            requestBodyI.setMachineType("projects/" + autoscalerConfiguration.getProject() +
                    "/zones/" + autoscalerConfiguration.getZone() +
                    "/machineTypes/" + autoscalerConfiguration.getMachineTypeBurstable());
            requestBodyI.setSourceMachineImage("projects/" + autoscalerConfiguration.getProject() +
                    "/global/machineImages/" + autoscalerConfiguration.getMachineImageBurstable());

            instanceGroup = autoscalerConfiguration.getInstanceGroupBurstable();

        } else if (InstanceType.ONDEMAND.equals(instanceType)){

            requestBodyI.setName(InstanceType.ONDEMAND.toString().toLowerCase()+"-"+uuid);

            requestBodyI.setMachineType("projects/" + autoscalerConfiguration.getProject() +
                    "/zones/" + autoscalerConfiguration.getZone() +
                    "/machineTypes/" + autoscalerConfiguration.getMachineTypeOndemand());
            requestBodyI.setSourceMachineImage("projects/" + autoscalerConfiguration.getProject()+
                    "/global/machineImages/" + autoscalerConfiguration.getMachineImageOndemand());

            instanceGroup= autoscalerConfiguration.getInstanceGroupOndemand();

        }
        Compute.Instances.Insert requestI =
                GoogleCloudAuth.computeService().instances().insert(autoscalerConfiguration.getProject(), autoscalerConfiguration.getZone(), requestBodyI);

        Operation responseI = requestI.execute();

        //adds to instance group
        InstanceGroupsAddInstancesRequest requestBodyIG = new InstanceGroupsAddInstancesRequest();
        List<InstanceReference> instances = new LinkedList<>();
        InstanceReference instanceReference = new InstanceReference();
        instanceReference.setInstance(responseI.getTargetLink());
        instances.add(instanceReference);
        requestBodyIG.setInstances(instances);

        Compute.InstanceGroups.AddInstances requestIG =
                GoogleCloudAuth.computeService().instanceGroups().addInstances(autoscalerConfiguration.getProject(), autoscalerConfiguration.getZone(), instanceGroup, requestBodyIG);

        Operation responseIG = requestIG.execute();

    }

    /**
     * Deletes an instance
     * @param instanceType ondemand or busrtable
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void removeInstanceFromGroup(InstanceType instanceType) throws IOException, GeneralSecurityException {

        String instanceGroup = null;
        InstanceGroupsListInstancesRequest requestBody = new InstanceGroupsListInstancesRequest();

        if (InstanceType.BURSTABLE.equals(instanceType)) {
            instanceGroup = autoscalerConfiguration.getInstanceGroupBurstable();
        } else if (InstanceType.ONDEMAND.equals(instanceType)) {
            instanceGroup = autoscalerConfiguration.getInstanceGroupOndemand();
        }

        Compute.InstanceGroups.ListInstances request =
                GoogleCloudAuth.computeService().instanceGroups().listInstances(autoscalerConfiguration.getProject(), autoscalerConfiguration.getZone(), instanceGroup, requestBody);

        InstanceGroupsListInstances response;


        response = request.execute();
        if (response.getItems() != null) {
            String[] partsOfLink = response.getItems().get(0).getInstance().split("/");
            String instanceToRemove = partsOfLink[partsOfLink.length-1];
                    Compute.Instances.Delete requestDelete =
                    GoogleCloudAuth.computeService().instances().delete(autoscalerConfiguration.getProject(), autoscalerConfiguration.getZone(), instanceToRemove);
            Operation responseDelete = requestDelete.execute();
        }
    }
}
