package com.jaimedantas.autoscaler.control;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
import com.jaimedantas.configuration.property.AutoscalerConfiguration;
import com.jaimedantas.configuration.authentication.GoogleCloudAuth;
import com.jaimedantas.enums.InstanceType;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class InstanceAllocation {

    @Inject
    AutoscalerConfiguration autoscalerConfiguration;

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

        List<NetworkInterface> networkInterfaces = new LinkedList<>();
        List<AliasIpRange> aliasIpRanges = new LinkedList<>();

        NetworkInterface networkInterface = new NetworkInterface();
        networkInterface.setKind("compute#networkInterface");
        networkInterface.setSubnetwork("projects/"+autoscalerConfiguration.getProject()+
                "/regions/"+autoscalerConfiguration.getRegion()+"/subnetworks/default");
        networkInterface.setAliasIpRanges(aliasIpRanges);
        networkInterfaces.add(networkInterface);

        requestBodyI.setNetworkInterfaces(networkInterfaces);

        if (InstanceType.BURSTABLE.equals(instanceType)) {
            requestBodyI.setName(InstanceType.BURSTABLE.toString().toLowerCase()+"-"+uuid);

            requestBodyI.setMachineType("projects/" + autoscalerConfiguration.getProject() +
                    "/zones/" + autoscalerConfiguration.getZone() +
                    "/machineTypes/" + autoscalerConfiguration.getMachineTypeBurstable());
            requestBodyI.setSourceMachineImage("projects/" + autoscalerConfiguration.getProject() +
                    "/global/machineImages/" + autoscalerConfiguration.getMachineImageBurstable());
            Map<String,String> labels = new HashMap<>();
            labels.put("instance-type", InstanceType.BURSTABLE.toString().toLowerCase());
            requestBodyI.setLabels(labels);

            instanceGroup = autoscalerConfiguration.getInstanceGroupBurstable();

        } else if (InstanceType.ONDEMAND.equals(instanceType)){

            requestBodyI.setName(InstanceType.ONDEMAND.toString().toLowerCase()+"-"+uuid);

            requestBodyI.setMachineType("projects/" + autoscalerConfiguration.getProject() +
                    "/zones/" + autoscalerConfiguration.getZone() +
                    "/machineTypes/" + autoscalerConfiguration.getMachineTypeOndemand());
            requestBodyI.setSourceMachineImage("projects/" + autoscalerConfiguration.getProject()+
                    "/global/machineImages/" + autoscalerConfiguration.getMachineImageOndemand());
            Map<String,String> labels = new HashMap<>();
            labels.put("instance-type", InstanceType.ONDEMAND.toString().toLowerCase());
            requestBodyI.setLabels(labels);

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
