<p align="center"><img src="/docs/BIAS_logo.png" height="30%" width="30%"> </p>

# BIAS Autoscaler
BIAS Autoscaler leverages burstable instances for scaling computer engines instances on GCP. It automatically scales out and in
compute engines on Google Cloud. Unlike most of the autoscalers available in the industry, BIAS Austoscaler 
make use of burstable instances for reducing the cost of cloud resources while increasing quality of service
metrics.

## Architecture
BIAS Autoscaler uses the Google API and Google SDK for scaling out and in the compute engine instance. The Cloud Load Balancing
is used for tuning the CPU usage of the burstable instances in real time. 

<p align="center"><img src="/docs/gcp_diagram.jpg" height="65%" width="65%"> </p>

## Languages and frameworks
 - Java 11
 - Micronaut Framework 2.4.1
 - Swagger UI
 - Lombock
 
### Google Cloud SDK and Libraries
 - [Compute Engine API Client Library for Java](https://github.com/googleapis/google-api-java-client-services/tree/master/clients/google-api-services-compute/beta)
 - [Google Cloud SDK](https://cloud.google.com/sdk/)

## Documentation
Swagger UI is used for documentation. You can find the information accessing the webpage below.
```
http://localhost:8080/swagger/views/swagger-ui/
```

## Building From Source
To build from source checkout the code and run:
```
mvn clean install
```

## Configuration
If you wish to run BIAS Austoscaler outside of Google Cloud environment, i.e. on your local machine, you
need to set the required credentials locally using the [Google Cloud SDK](https://cloud.google.com/sdk/).
