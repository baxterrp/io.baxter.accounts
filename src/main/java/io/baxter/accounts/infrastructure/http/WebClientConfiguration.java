package io.baxter.accounts.infrastructure.http;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    @Qualifier("authServiceWebClient")
    public WebClient authServiceWebClient(){
        return WebClient
                .builder()
                .baseUrl("http://host.docker.internal:9000")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
