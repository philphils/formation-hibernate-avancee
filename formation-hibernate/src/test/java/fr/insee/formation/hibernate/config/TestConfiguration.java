package fr.insee.formation.hibernate.config;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.insee.formation.hibernate.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
@ActiveProfiles("test")
public class TestConfiguration {

}