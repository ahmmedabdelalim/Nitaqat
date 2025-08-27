package com.nitaqat.nitaqat.service;

import com.nitaqat.nitaqat.dto.ActivityHierarchyDTO;
import com.nitaqat.nitaqat.entity.Activity;
import com.nitaqat.nitaqat.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }


    public List<ActivityHierarchyDTO> getHierarchy() {
        List<Activity> allActivities = activityRepository.findAll();

        // Map primaryColumn -> DTO
        Map<Integer, ActivityHierarchyDTO> dtoMap = new HashMap<>();
        for (Activity act : allActivities) {
            dtoMap.put(
                    act.getPrimaryColumn(),
                    new ActivityHierarchyDTO(
                            act.getPrimaryColumn(),
                            act.getName(),
                            act.getCompanyCode(),
                            act.getPercentage()
                    )
            );
        }

        // Build hierarchy
        List<ActivityHierarchyDTO> roots = new ArrayList<>();
        for (Activity act : allActivities) {
            ActivityHierarchyDTO dto = dtoMap.get(act.getPrimaryColumn());

            if (act.getParentId() == null) {
                // no parent → root
                roots.add(dto);
            } else {
                ActivityHierarchyDTO parent = dtoMap.get(act.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                    parent.setHasChildren(true);
                } else {
                    // if parent not found, treat as root
                    roots.add(dto);
                }
            }
        }

        return roots;
    }
}
