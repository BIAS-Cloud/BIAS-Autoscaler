<p align="center"><img src="https://bias-cloud.github.io/BIAS-Autoscaler/docs/img/BIAS_logo.png" height="40%" width="40%"> </p>

# BIAS Autoscaler
BIAS Autoscaler leverages burstable instances for scaling Google Computer Engine instances on GCP. Unlike most of the autoscalers available in the industry, BIAS Austoscaler 
uses burstable instances for reducing cost and increasing efficiency of cloud resources while maintaining quality of service
metrics.

## Architecture
BIAS Autoscaler uses the Google API and Google SDK for scaling out and in Google Compute Engine instances. The Google Cloud Load Balancer
is used for tuning the CPU usage of the burstable instances in real time. 

<p align="center"><img src="https://bias-cloud.github.io/BIAS-Autoscaler/docs/img/GCP_diagram.png" height="70%" width="70%"> </p>

### Internal design
The internal architecture of the BIAS Autoscaler consists of three major blocks: Controller, Monitor and Scaling Policy.

<p align="center"><img src="https://bias-cloud.github.io/BIAS-Autoscaler/docs/img/BIAS_architecture.jpg" height="50%" width="50%"> </p>

## Languages and frameworks
 - Java 11
 - Micronaut Framework 2.4.1
 - Swagger UI
 - Lombock  
 
### Google Cloud SDK and Libraries
 - [Google Compute Engine API Client Library for Java](https://github.com/googleapis/google-api-java-client-services/tree/master/clients/google-api-services-compute/beta)
 - [Google Stackdriver Monitoring Client for Java](https://github.com/googleapis/java-monitoring)
 - [Google Cloud SDK](https://cloud.google.com/sdk/)

## Requirements
- JDK 11
- Maven 3
- Google Cloud SDK

## Documentation
All documentation is available [here](/docs/README.md).

#### Index

1. [Quick Start](docs/src/1-quick-start.md)
   - [Run Autoscaler](docs/src/1-1-run.md)
   - [Configure Autoscaler](docs/src/1-2-configure.md)
2. [Scaling Policy](docs/src/2-scaling-policy.md)
3. [Autoscaler Architecture](docs/src/3-architecture.md)
   - [Monitor](docs/src/3-1-monitor.md)
   - [Scaling](docs/src/3-2-scaling.md)
   - [Controller](docs/src/3-3-controller.md)
4. [Benchmark Tests](docs/src/4-benchmark-tests.md)
5. [APIs](docs/src/5-apis.md)

### Citation
This work was published in the WoSC7:

Jaime Dantas, Hamzeh Khazaei and Marin Litoiu. 2021. BIAS Autoscaler: Leveraging Burstable Instances for Cost-Effective Au- toscaling on Cloud Systems. In *Seventh International Workshop on Serverless Computing (WoSC7) 2021 (WoSC ’21), December 6, 2021, Virtual Event, Canada.* ACM, New York, NY, USA, 8 pages. https://doi.org/10.1145/3493651.3493667

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
