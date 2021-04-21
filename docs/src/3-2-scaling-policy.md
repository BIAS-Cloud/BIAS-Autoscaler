# Scaling Policy
This component is where the scaling algorithm is implemented. 
It reads the metrics provided by the monitor component, 
and calculates the number of burstable and regular instances 
of the current demand. This information is then fed to the controller 
module so it can perform the scaling of the cluster. Currently, 
BIAS Autoscaler supports only the Square-Root Staffing Rule (SR Rule) 
scaling policy, but any policy can be applied. This is because all 
information regarding time-series data of the metrics is already 
provided by the monitor module, so even a predictive model can be 
easily added on BIAS Autoscaler. This module receives all the metrics 
(CPU utilization, number of each instances, and weights and arrival rate 
of each instance type from the load balancer) as input, and outputs the 
predicted number of burstable and regular instances.

### Implementing Your Own Scaling Algorithm
Any scaling strategy can be used on BIAS Autoscaler. In order to do so, you need to create
a implement the same methods as the ones on [SquareRootStaffing](https://github.com/jaimedantas/BIAS-autoscaler/blob/main/src/main/java/com/jaimedantas/autoscaler/scaling/SquareRootStaffing.java).
```
autoscaler
└───scaling
    └──Resource.java
    └──ResourceValidator.java
    └──SquareRootStaffing.java
```
Only two methods are required: one that outputs de number of burstable instances, and 
another one for the regular ones. The example below inlustrates how you can implement your onw methods for scaling. 
 
 ```java
    /**
     * Calculates the Square Root Stafding k = R + c * sqrt(R)
     * The regular instances is R
     * @param r arrival/mu
     * @return the number of regular instances RD
     */
    public int calculateNumberOfRegularInstances(long r){
        return (int) r;
    }

    /**
     * Calculates the Square Root Stafding k = R + c * sqrt(R)
     * The burstable instances is c * sqrt(R)
     * @param r arrival/mu
     * @return the number of burstable instances (k - R)
     */
    public int calculateNumberOfBurstableInstances(long r) throws InvalidProbabilityQueueException {
        return (int) Math.round(r + getProbabilityQueue() * Math.sqrt(r));
    }
```
If your strategy needs to read the current and past metrics, you can do so by calling any of the methods 
from the monitor block. For example, if you need the total number of instances, just call `getNumberOfInstances()`.

![](../img/BIAS_flowchart.png)

### Index

1. [Quick Start](../src/1-quick-start.md)
   - [Run autoscaler](../src/1-1-run.md)
   - [Configure autoscaler](../src/1-2-configure.md)
2. [Scaling Policy](../src/2-scaling-policy.md)
3. [Autoscaler Architecture](../src/3-architecture.md)
   - [Monitor](../src/3-1-monitor.md)
   - [Scaling Policy](../src/3-2-scaling-policy.md)
   - [Controller](../src/3-3-controller.md)
4. [Benchmark Tests](../src/4-benchmark-tests.md)
   - [Burstable Instances](../src/4-1-burstable.md)
   - [Regular Instances](../src/4-2-regular.md)
5. [APIs](../src/5-apis.md)
