package dev.falae.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "dev.falae.application",
        "dev.falae.core",
        "dev.falae.infrastructure"
})
@EnableJpaRepositories(basePackages = "dev.falae.infrastructure.adapters.repositories.jpa")
@EntityScan(basePackages = "dev.falae.infrastructure.adapters.repositories.entities")
public class FalaeDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(FalaeDevApplication.class, args);
	}

}
