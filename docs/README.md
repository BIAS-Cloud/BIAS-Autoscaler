<p align="center"><img src="https://jaimedantas.com/BIAS-autoscaler/docs/img/BIAS_logo.png" height="40%" width="40%"> </p>

## BIAS Autoscaler
Burstable instances have recently been introduced by cloud provides as a
cost-efficient alternative to customers that do not require powerful machines
for running their workloads. Unlike conventional instances, the CPU capacity
of burstable instances is rate limited, but they can be boosted to their maximum
capacity for small periods when needed. Currently, the majority of cloud providers
offer this option as a cheaper solution for their clients. However, little research
has been done on the practical usage of these CPU-limited instances. 

BIAS Autoscaler is a novel autoscaling solution that uses burstable 
instances along with regular instances to handle the queueing arising in traffic. 
Experimental results show that BIAS Autoscaler 
can reduce the overall cost up to 25% and increase cluster efficiency in 
42% while maintaining the same metrics of Quality of Services observed when
 using conventional instances only.

### Index

1. [Quick Start](src/1-quick-start.md)
   - [Run Autoscaler](src/1-1-run.md)
   - [Configure Autoscaler](src/1-2-configure.md)
2. [Scaling Policy](src/2-scaling-policy.md)
3. [Autoscaler Architecture](src/3-architecture.md)
   - [Monitor](src/3-1-monitor.md)
   - [Scaling Policy](src/3-2-scaling-policy.md)
   - [Controller](src/3-3-controller.md)
4. [Benchmark Tests](src/4-benchmark-tests.md)
5. [APIs](src/5-apis.md)
