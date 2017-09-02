package com.fantank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fantank.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	User findById(long id);
}
