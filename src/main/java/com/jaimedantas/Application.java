package com.jaimedantas;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
    info = @Info(
            title = "bias-autoscaler",
            description = "BIAS Autoscaler for Google Cloud Compute Instances",
            version = "0.1",
            license = @License(name = "MIT License", url = "https://github.com/jaimedantas/BIAS-autoscaler/blob/main/LICENSE"),
            contact = @Contact(url = "https://jaimedantas.com", name = "Jaime", email = "jaimejales@hotmail.com")
    )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
