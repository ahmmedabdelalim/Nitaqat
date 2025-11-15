package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
    List<User> findByRoleAndActive(String role , boolean active);
}
