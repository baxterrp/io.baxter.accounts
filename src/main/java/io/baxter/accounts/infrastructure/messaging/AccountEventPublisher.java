package io.baxter.accounts.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "AccountRegistered";

    public void publishAccountRegistered(AccountRegisteredEvent event) {
        log.info("Publishing AccountRegistered event: {}", event);
        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish AccountRegistered event: {}", ex.getMessage());
                    } else {
                        log.info("Event published successfully to topic: {}", TOPIC);
                    }
                });
    }
}