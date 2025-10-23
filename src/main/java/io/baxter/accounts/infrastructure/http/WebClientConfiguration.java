package io.baxter.accounts.infrastructure.http;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    private final String authServiceUrl;

    public WebClientConfiguration(@Value("${AUTH_SERVICE_URL}") String authServiceUrl){
        this.authServiceUrl = authServiceUrl;
    }

    @Bean
    @Qualifier("authServiceWebClient")
    public WebClient authServiceWebClient(){
        return WebClient
                .builder()
                .baseUrl(authServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
