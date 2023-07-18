package fr.insee.formation.hibernate;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.insee.formation.hibernate.repositories.custom.impl.JPAHistoRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "fr.insee.formation.hibernate.repositories", repositoryBaseClass = JPAHistoRepositoryImpl.class, considerNestedRepositories = true)
@EnableBatchProcessing
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}