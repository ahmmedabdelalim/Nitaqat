package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.entity.UserActivityDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface UserActivityDailyRepository extends JpaRepository<UserActivityDaily, Long> {
    Optional<UserActivityDaily> findByUserIdAndActivityDate(Long userId, LocalDate date);
}