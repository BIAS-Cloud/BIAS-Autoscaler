package com.jaimedantas.autoscaler.scaling;

import com.jaimedantas.enums.InstanceType;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.HashMap;


@MicronautTest(propertySources = "application.yml")
@Property(name = "scaling.maximumBurstableInstances", value = "1")
class ResourceValidatorTest {

    @Inject
    ResourceValidator resourceValidator;

    @Test
    @Property(name = "scaling.maximumRegularInstances", value = "8")
    void validateNumberOfInstances() {
        Assertions.assertEquals(8, resourceValidator.validateRegularInstances(10));
        Assertions.assertEquals(8, resourceValidator.validateBurstableInstances(10));
    }

    @Test
    @Property(name = "scaling.maximumInstances", value = "8")
    void validateTotalNumberOfInstances() {
        HashMap<InstanceType, Integer> hashMap = resourceValidator.validateResources(15,3);
        Assertions.assertEquals(7, hashMap.get(InstanceType.ONDEMAND));
        Assertions.assertEquals(1, hashMap.get(InstanceType.BURSTABLE));
    }

    @Test
    void validateTotalNumberOfInstancesZero() {
        HashMap<InstanceType, Integer> hashMap = resourceValidator.validateResources(0,0);
        Assertions.assertEquals(1, hashMap.get(InstanceType.ONDEMAND));
        Assertions.assertEquals(1, hashMap.get(InstanceType.BURSTABLE));
    }

}
