package com.fantank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fantank.model.Webhook;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
	Webhook findById(String id);
}
