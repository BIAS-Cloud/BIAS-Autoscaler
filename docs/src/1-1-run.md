# Run
You can run BIAS Autoscaler on a Google Compute Engine or as a pod on Google Kubernetes
Engine.

## Requirements
- JDK 11
- Maven 3
- Google Cloud SDK

## Building From Source
To build from source, checkout the code and run:
```
$ mvn clean install
```
## Run
To run the BIAS Autoscaler, run:

```
$ java -jar bias-autoscaler-0.1.jar
```

## Startup
BIAS Autoscaler will make sure the cluster has the same configuration as the property
file during startup time. In the log below, you can see that BIAS Autoscaler scales out two 
instances during the startup since the cluster had less instances than the minimum defined. 

```
 __  __ _                                  _   
|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_ 
| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|
| |  | | | (__| | | (_) | | | | (_| | |_| | |_ 
|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|
  Micronaut (v2.4.1)

18:08:17.658 [main] INFO  i.m.context.env.DefaultEnvironment - Established active environments: [prod]
18:08:19.774 [main] WARN  c.j.autoscaler.Orchestrator - Running startup check on cluster
18:08:23.431 [main] WARN  c.j.autoscaler.Orchestrator - Scaling out BURSTABLE instance
18:08:27.230 [main] WARN  c.j.autoscaler.Orchestrator - Scaling out REGULAR instance
```


## Execution Log
BIAS Autoscaler will log evey action it performs. Below you can find a single run where BIAS
Autoscaler reads all metrics, calculates the future demand, and scales out the cluster.
```
01:49:20.835 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Running the scheduler for the Autoscaler
01:49:23.045 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Arrival Rate: 1145 req/min
01:49:23.046 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - CPU of BURSTABLE: 45.54%
01:49:23.046 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - CPU of REGULAR: 66.42%
01:49:23.046 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Predicted REGULAR = 2
01:49:23.046 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Predicted BURSTABLE = 2
01:49:24.376 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Current REGULAR = 1
01:49:24.376 [scheduled-executor-thread-4] INFO  c.j.autoscaler.Orchestrator - Current BURSTABLE = 1
01:49:24.376 [scheduled-executor-thread-4] WARN  c.j.autoscaler.Orchestrator - Updating CPU utilization of IG instance-group-burstable to 1.0
01:49:26.538 [scheduled-executor-thread-4] WARN  c.j.autoscaler.Orchestrator - Scaling out REGULAR instance
01:49:29.607 [scheduled-executor-thread-4] WARN  c.j.autoscaler.Orchestrator - Scaling out BURSTABLE instance
```


### Index

1. [Quick Start](../src/1-quick-start.md)
   - [Run Autoscaler](../src/1-1-run.md)
   - [Configure Autoscaler](../src/1-2-configure.md)
2. [Scaling Policy](../src/2-scaling-policy.md)
3. [Autoscaler Architecture](../src/3-architecture.md)
   - [Monitor](../src/3-1-monitor.md)
   - [Scaling Policy](3-2-scaling.md)
   - [Controller](../src/3-3-controller.md)
4. [Benchmark Tests](../src/4-benchmark-tests.md)
5. [APIs](../src/5-apis.md)
