# BIAS Autoscaler
BIAS Autoscaler leverages burstable instances for scaling computer engines on GCP. It automatically scales out and in
compute engines on Google Cloud. Unlike most of the autoscalers available in the industry, BIAS Austoscaler 
make use of burstable instances for reducing the cost of cloud resources while increasing quality of service
metrics.

## Languages and libraries
 - Java 11
 - Micronaut Framework
 - Swagger UI
 - Lombock
 
### Google Cloud SDK and Libraries
 -[Compute Engine API Client Library for Java](https://github.com/googleapis/google-api-java-client-services/tree/master/clients/google-api-services-compute/beta)
 -[Google Cloud SDK](https://cloud.google.com/sdk/)

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
