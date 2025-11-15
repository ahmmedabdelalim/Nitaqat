package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ActivityDto;
import com.nitaqat.nitaqat.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    void deleteByUserId(Long userId);

    List<Activity> findByUserId(Long userId);


    @Query("SELECT new com.nitaqat.nitaqat.dto.ActivityDto(a.id, a.name) FROM Activity a")
    List<ActivityDto> findAllIdAndName();

    // Return only id and name for a specific user
    @Query("SELECT new com.nitaqat.nitaqat.dto.ActivityDto(a.id, a.name) FROM Activity a WHERE a.user.id = :userId")
    List<ActivityDto> findAllIdAndNameByUserId(@Param("userId") Long userId);


}
