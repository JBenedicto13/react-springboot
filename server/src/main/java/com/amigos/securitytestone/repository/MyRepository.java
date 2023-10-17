package com.amigos.securitytestone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amigos.securitytestone.model.User;

public interface MyRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
