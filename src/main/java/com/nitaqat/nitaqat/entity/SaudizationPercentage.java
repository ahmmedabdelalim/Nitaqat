package com.nitaqat.nitaqat.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "saudization_percentage")
public class SaudizationPercentage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="primary_column")
    private Long PrimaryColumn;

    @Column(name="job")
    private String job;

    @Column(name="job_ar")
    private String jobAr;

    @Column(name="job_en")
    private String jobEn;

    @Column(name="saudization_catageory")
    private String saudizationCatageory;

    @Column(name="saudization_catageory_ar")
    private String saudizationCatageoryAr;

    @Column(name="saudization_percentage")
    private Long saudizationPercentage;



    @Column(name="emp_threshold")
    private Long empThreshold;

    public Long getEmpThreshold() {
        return empThreshold;
    }

    public void setEmpThreshold(Long empThreshold) {
        this.empThreshold = empThreshold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrimaryColumn() {
        return PrimaryColumn;
    }

    public void setPrimaryColumn(Long primaryColumn) {
        PrimaryColumn = primaryColumn;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobAr() {
        return jobAr;
    }

    public void setJobAr(String jobAr) {
        this.jobAr = jobAr;
    }

    public String getJobEn() {
        return jobEn;
    }

    public void setJobEn(String jobEn) {
        this.jobEn = jobEn;
    }

    public String getSaudizationCatageory() {
        return saudizationCatageory;
    }

    public void setSaudizationCatageory(String saudizationCatageory) {
        this.saudizationCatageory = saudizationCatageory;
    }

    public String getSaudizationCatageoryAr() {
        return saudizationCatageoryAr;
    }

    public void setSaudizationCatageoryAr(String saudizationCatageoryAr) {
        this.saudizationCatageoryAr = saudizationCatageoryAr;
    }

    public Long getSaudizationPercentage() {
        return saudizationPercentage;
    }

    public void setSaudizationPercentage(Long saudizationPercentage) {
        this.saudizationPercentage = saudizationPercentage;
    }


}
