package com.fantank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fantank.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	public Role findByName(String name);
	public List<Role> findAllByName(String name);
}
