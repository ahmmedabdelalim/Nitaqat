package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.entity.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionsRepository extends JpaRepository<Profession, Long> {

    void deleteByUserId(Long userId);


    List<Profession> findByUserId(Long userId);
}
