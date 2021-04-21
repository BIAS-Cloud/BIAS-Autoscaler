# Quick Start
In order to use BIAS Autoscaler on GCP, the cluster needs to be configured to support
 external autoscaling. To do this, the first step required is to create two instance
  groups, one for each instance type. The user can choose to create an instance 
  template to use across these instance groups. Then, the Google Load Balancer 
  needs to be configured so it can distribute the traffic between these two instance
   groups based on the average CPU utilization of the instances. This will allow 
   BIAS Autoscaler to dynamically update the weights of the burstable instance group.



BIAS Autoscaler has a configuration file where the user adds all the cluster 
information. There are also variables to adjusts the scaling frequency, 
minimum and maximum instances of each group, delay for scaling out/in, overprovisioning configuration and other
properties concerning the scaling policy used. Finally, BIAS Autoscaler 
can run either in a container or inside a Google Compute Engine. 
Since it uses the Google Cloud SDK, no authentication configuration 
is required when running it inside the same cluster

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
