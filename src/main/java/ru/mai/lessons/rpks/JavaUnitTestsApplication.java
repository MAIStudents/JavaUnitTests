package ru.mai.lessons.rpks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class JavaUnitTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaUnitTestsApplication.class, args);
	}
}
