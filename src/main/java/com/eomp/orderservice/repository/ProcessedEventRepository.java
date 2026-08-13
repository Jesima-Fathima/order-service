package com.eomp.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eomp.orderservice.entity.ProcessedEvent;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByEventId(UUID eventId);
}