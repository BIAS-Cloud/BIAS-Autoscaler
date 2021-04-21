# Controller
This module is the core of BIAS Autoscaler. 
The number of burstable and regular instances is provided to this component,
 and it outputs the necessary changes to the cluster. It uses the Google 
 Cloud Java API to control the load balancer traffic distribution of 
 the instance groups, and scales out/in the Google Compute Engine 
 instances. The user can set the frequency by which it performs
  the autoscaling, or use the default one which is 60 seconds. 
  The flowchart on figure 3 shows how these modules communicate and 
  perform the autoscaling.

![](../img/BIAS_flowchart.png)

### Index

1. [Quick Start](../src/1-quick-start.md)
   - [Run Autoscaler](../src/1-1-run.md)
   - [Configure Autoscaler](../src/1-2-configure.md)
2. [Scaling Policy](../src/2-scaling-policy.md)
3. [Autoscaler Architecture](../src/3-architecture.md)
   - [Monitor](../src/3-1-monitor.md)
   - [Scaling Policy](../src/3-2-scaling-policy.md)
   - [Controller](../src/3-3-controller.md)
4. [Benchmark Tests](../src/4-benchmark-tests.md)
5. [APIs](../src/5-apis.md)
