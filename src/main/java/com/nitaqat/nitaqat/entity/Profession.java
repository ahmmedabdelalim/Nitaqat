package com.nitaqat.nitaqat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "professions")

public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "border_number")
    private String borderNumber;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "job")
    private String job;

    @Column(name = "residence_expire_date")
    private LocalDate residenceExpireDate;

    @Column(name = "date_of_entry_inot_theKingdom")
    private LocalDate dateOfEntryIntoTheKingdom;

    @Column(name = "work_type")
    private String workType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBorderNumber() {
        return borderNumber;
    }

    public void setBorderNumber(String borderNumber) {
        this.borderNumber = borderNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public LocalDate getResidenceExpireDate() {
        return residenceExpireDate;
    }

    public void setResidenceExpireDate(LocalDate residenceExpireDate) {
        this.residenceExpireDate = residenceExpireDate;
    }

    public LocalDate getDateOfEntryIntoTheKingdom() {
        return dateOfEntryIntoTheKingdom;
    }

    public void setDateOfEntryIntoTheKingdom(LocalDate dateOfEntryIntoTheKingdom) {
        this.dateOfEntryIntoTheKingdom = dateOfEntryIntoTheKingdom;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}
