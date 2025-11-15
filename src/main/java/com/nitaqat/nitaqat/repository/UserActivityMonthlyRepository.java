package com.nitaqat.nitaqat.repository;


import com.nitaqat.nitaqat.entity.UserActivityMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserActivityMonthlyRepository extends JpaRepository<UserActivityMonthly, Long> {
    Optional<UserActivityMonthly> findByUserIdAndMonth(Long userId, String month);
}

