<p align="center"><img src="/docs/img/BIAS_logo.png" height="40%" width="40%"> </p>

# BIAS Autoscaler
BIAS Autoscaler leverages burstable instances for scaling Google Computer Engine instances on GCP. Unlike most of the autoscalers available in the industry, BIAS Austoscaler 
uses burstable instances for reducing cost and increasing efficiency of cloud resources while maintaining quality of service
metrics.

## Architecture
BIAS Autoscaler uses the Google API and Google SDK for scaling out and in Google Compute Engine instances. The Google Cloud Load Balancer
is used for tuning the CPU usage of the burstable instances in real time. 

<p align="center"><img src="/docs/img/GCP_diagram.png" height="70%" width="70%"> </p>

### Internal design
The internal architecture of the BIAS Autoscaler consists of three major blocks: Controller, Monitor and Scaling Policy.

<p align="center"><img src="/docs/img/BIAS_architecture.jpg" height="50%" width="50%"> </p>

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
All documentation is available [here]()
Swagger UI is used for documentation. You can find the information accessing the webpage below.
```
http://localhost:8080/swagger/views/swagger-ui/
```
You can manage your cluster using the Swagger interface of the BIAS Autoscaler.  

<p align="center"><img src="/docs/img/BIAS_Swagger_print.png" height="50%" width="50%"> </p>

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

## Configuration
If you wish to run BIAS Austoscaler outside of Google Cloud environment, i.e., on your local machine, you
need to set the required credentials locally using the [Google Cloud SDK](https://cloud.google.com/sdk/).
