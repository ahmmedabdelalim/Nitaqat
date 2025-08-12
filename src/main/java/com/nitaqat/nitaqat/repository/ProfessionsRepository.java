package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.entity.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionsRepository extends JpaRepository<Profession, Long> {
}
