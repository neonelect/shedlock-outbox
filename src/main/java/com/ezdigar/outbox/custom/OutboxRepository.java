package com.ezdigar.outbox.custom;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OutboxRepository extends CrudRepository<Outbox, String> {

    Iterable<Outbox> findByStatusIs(MessageStatus messageStatus);

}
