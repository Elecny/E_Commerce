package com.example.e_commerce.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.e_commerce.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    
}