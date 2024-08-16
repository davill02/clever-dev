package com.cleverdev.testtask.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient_note")
public class PatientNote {
    @Id
    @GeneratedValue
    private Long id;
    private String note;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private CompanyUser createdByUserId;

    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private CompanyUser lastModifiedByUserId;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;

    private String oldGuid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PatientProfile getPatient() {
        return patient;
    }

    public void setPatient(PatientProfile patient) {
        this.patient = patient;
    }

    public CompanyUser getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(CompanyUser createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public CompanyUser getLastModifiedByUserId() {
        return lastModifiedByUserId;
    }

    public void setLastModifiedByUserId(CompanyUser lastModifiedByUserId) {
        this.lastModifiedByUserId = lastModifiedByUserId;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getOldGuid() {
        return oldGuid;
    }

    public void setOldGuid(String oldGuid) {
        this.oldGuid = oldGuid;
    }

}
