## Configuration
The [application.yml](https://github.com/jaimedantas/BIAS-autoscaler/blob/main/src/main/resources/application.yml) 
file has all the properties required to run BIAS Autoscaler on Google Cloud. Bear in mid it needs to 
run in the same cluster you are performing the autoscaler. It you wish to run in a different
cluster, you need to configure the Google SDK to autenticate the pod/VM to your cluster. 
### Properties

```yml
autoscaler:
  project: # name of your project
  zone: # zone of your project 
  region: # region of your project
  machine-type-ondemand: # VM type for the regular instacens
  instance-group-ondemand: # instance group for regular instances
  monitoring-group-ondemand: # group ID for regular instances
  machine-image-ondemand: # name of image for regular instacens
  machine-type-burstable: # VM type for the burstable instacens
  instance-group-burstable: # instance group for burstable instances
  monitoring-group-burstable: # group ID for regular instances
  machine-image-burstable: # name of image for burstable instacens
  backend-service: # name of the service for load balancing configuration

scaling:
  maximum-regular-instances: # max # regular of instances
  maximum-burstable-intances: # max # burstable of instances
  maximum-instances: # min # of instances
  minimum-regular-instances: # min # regular of instances
  minimum-burstable-instances: # max # burstable of instances
  current-regular-instances: # total # of regular insntaces for the startup
  current-burstable-instances: # total # of burstable insntaces for the startup
  cpu-utilization-burstable: 0.4 # CPU weight of burstable instances ( from 0 to 1)
  probability-queueing: 0.1 # refer to scalling policy SR Rule
  requests-samples: 3 # waiting time to new scaling
  cpu-samples: 3 # waiting time to new scaling
  autoscaler-decision-interval: 60s # frequency for running the autoscaler
  autoscaler-scale-waiting-time: 90 # time by witch the autoscaler will wait to the next scale out/in in seconds
  mu: 1000 # mu is the capacity in minuted for the SR Rule where R = arrival/mu
  m: 1.0 # overprovisioning constant using burstable instances
```

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
