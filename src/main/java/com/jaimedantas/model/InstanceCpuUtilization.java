package com.jaimedantas.model;

import com.google.protobuf.Timestamp;
import com.jaimedantas.enums.InstanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstanceCpuUtilization {

    double value;
    Timestamp timestamp;
    InstanceType instanceType;

}
