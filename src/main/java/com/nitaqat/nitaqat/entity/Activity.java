package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activities")

public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="primary_cloumn")
    private Integer primaryColumn;

    private String name;

    @Column(name = "company_code")
    private String companyCode;


    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "percentage")
    private Long percentage;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrimaryColumn() {
        return primaryColumn;
    }

    public void setPrimaryColumn(Integer primaryColumn) {
        this.primaryColumn = primaryColumn;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Long getPercentage() {
        return percentage;
    }

    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }
}
