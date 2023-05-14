package com.ezdigar.outbox.custom;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
public class OutboxRetryTask {
    private OutboxRepository outboxRepository;
    private RestTemplate restTemplate;

    @Autowired
    public OutboxRetryTask(OutboxRepository outboxRepository, RestTemplate restTemplate) {
        this.outboxRepository = outboxRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @SchedulerLock(name = "currentTimeTask", lockAtLeastFor = "5S", lockAtMostFor = "10M")
    public void retry() {
        log.info("Searching for pending messages...");
        Iterable<Outbox> outboxEntities = outboxRepository.findByStatusIs(MessageStatus.PENDING);
        for (Outbox outbox : outboxEntities) {
            try {
                log.info("Sending pending messages...");
                if ((new Random().nextInt(9) + 1) % 2 != 0) {
                    log.info("Throwing random error...");
                    throw new NullPointerException();
                } else {
                    log.info(restTemplate.getForEntity("https://645f547a9d35038e2d210a45.mockapi.io/testGet", String.class).getBody());
                }
                outbox.setStatus(MessageStatus.SENT);
                outboxRepository.save(outbox);
                log.info("Message sent!");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}