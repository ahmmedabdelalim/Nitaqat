package com.nitaqat.nitaqat.controller;


import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

//    @GetMapping("/api/activities")
//    public ResponseEntity<List<Activity>> getActivityTree() {
//        return ResponseEntity.ok(activityService.getAllParentWithChildren());
//    }
}
