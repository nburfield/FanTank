package com.fantank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fantank.model.Investment;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
	InvestmentRepository findByInvestmentId(String investmentId);
	InvestmentRepository findByUserId(long userId);
}
