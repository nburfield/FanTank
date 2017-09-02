package com.fantank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fantank.model.Offering;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
	public Offering findById(String id);
	public Offering findByOfferingId(String offeringId);
}
