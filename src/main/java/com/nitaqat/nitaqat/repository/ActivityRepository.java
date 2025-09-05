package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ActivityDto;
import com.nitaqat.nitaqat.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT new com.nitaqat.nitaqat.dto.ActivityDto(a.id, a.name) FROM Activity a")
    List<ActivityDto> findAllIdAndName();


}
