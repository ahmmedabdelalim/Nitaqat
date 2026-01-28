package com.nitaqat.nitaqat.dto;

import java.util.ArrayList;
import java.util.List;

public class ActivityHierarchyDTO {

    private Integer id;
    private String name;
    private String companyCode;
    private Long percentage;
    private boolean hasChildren;
    private List<ActivityHierarchyDTO> children = new ArrayList<>();

    public ActivityHierarchyDTO(Integer id, String name, String companyCode, Long percentage) {
        this.id = id;
        this.name = name;
        this.companyCode = companyCode;
        this.percentage = percentage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Long getPercentage() {
        return percentage;
    }

    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public List<ActivityHierarchyDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ActivityHierarchyDTO> children) {
        this.children = children;
    }
}
