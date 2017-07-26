package com.fantank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fantank.model.User;
import com.fantank.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
