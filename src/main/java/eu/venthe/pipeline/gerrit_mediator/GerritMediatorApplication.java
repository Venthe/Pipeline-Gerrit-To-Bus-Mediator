package eu.venthe.pipeline.gerrit_mediator;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class GerritMediatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerritMediatorApplication.class, args);
	}
}
