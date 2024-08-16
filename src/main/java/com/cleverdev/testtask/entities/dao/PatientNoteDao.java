package com.cleverdev.testtask.entities.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cleverdev.testtask.entities.PatientNote;

@Repository
public interface PatientNoteDao extends JpaRepository<PatientNote, Long> {
    Optional<PatientNote> findByOldGuid(String oldGuid);
}
