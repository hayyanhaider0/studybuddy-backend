package com.studybuddy.backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.UserDetails;

public interface UserRepository extends MongoRepository<UserDetails, String> {
    Optional<UserDetails> findByUsername(String username);

    Optional<UserDetails> findByEmail(String email);
}
