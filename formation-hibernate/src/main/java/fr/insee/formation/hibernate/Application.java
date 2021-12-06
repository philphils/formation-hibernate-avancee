package fr.insee.formation.hibernate;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "fr.insee.formation.hibernate.repositories")
@EnableBatchProcessing
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
