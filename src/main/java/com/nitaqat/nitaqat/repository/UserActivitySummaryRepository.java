package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.entity.UserActivitySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserActivitySummaryRepository extends JpaRepository<UserActivitySummary, Long> {
    Optional<UserActivitySummary> findByUserId(Long userId);
}
