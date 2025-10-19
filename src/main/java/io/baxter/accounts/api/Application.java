package io.baxter.accounts.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = {
        "io.baxter.accounts.api",
        "io.baxter.accounts.infrastructure",
        "io.baxter.accounts.data"
})
@EnableR2dbcRepositories(basePackages = "io.baxter.accounts.data.repository")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
