package com.ezdigar.outbox.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public record OrderService(
    RestTemplate restTemplate,
    OutboxRepository outboxRepository,
    TransactionTemplate transactionTemplate) {

    public void create(String message) {
        UUID outboxId = UUID.randomUUID();

        Outbox outbox = new Outbox(outboxId.toString(), message, MessageStatus.PENDING);

        log.info("Saving message...");

        transactionTemplate.executeWithoutResult(transactionStatus ->
            outboxRepository.save(outbox)
        );

        if((new Random().nextInt(9) + 1) % 2 != 0) {
            log.info("Throwing random error...");
            throw new NullPointerException();
        } else {
            log.info(restTemplate.getForEntity("https://645f547a9d35038e2d210a45.mockapi.io/testGet", String.class).getBody());
        }

        outbox.setStatus(MessageStatus.SENT);
        
        // it is better to execute this line asynchonically
        outboxRepository.save(outbox);
    }

}