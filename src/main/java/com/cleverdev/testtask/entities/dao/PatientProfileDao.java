package com.cleverdev.testtask.entities.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cleverdev.testtask.entities.PatientProfile;

@Repository
public interface PatientProfileDao extends JpaRepository<PatientProfile, Long> {
    @Query("FROM PatientProfile WHERE oldClientGuid LIKE ?1 AND statusId IN (200, 210, 230)")
    Optional<PatientProfile> getProfileIfActive(String guidLike);
}
