package com.jaimedantas.model;

import com.google.protobuf.Timestamp;
import com.jaimedantas.enums.InstanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ArrivalRate {

    long value;
    Timestamp timestamp;
    InstanceType instanceType;

}

