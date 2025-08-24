package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

//    public List<Activity> getAllParentWithChildren() {
//        return activityRepository.findByParentIsNull(); // parent + children
//    }
}
