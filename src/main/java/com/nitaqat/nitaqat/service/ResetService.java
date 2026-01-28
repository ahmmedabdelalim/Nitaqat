package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.repository.ActivityRepository;
import com.nitaqat.nitaqat.repository.ProfessionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResetService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProfessionsRepository professionRepository;

    public void resetUserData(Long userId) {
        // Delete safely (only rows with matching userId)
        professionRepository.deleteByUserId(userId);
        activityRepository.deleteByUserId(userId);
    }
}
