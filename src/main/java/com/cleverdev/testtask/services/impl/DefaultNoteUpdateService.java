package com.cleverdev.testtask.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleverdev.testtask.dto.OldSystemNote;
import com.cleverdev.testtask.entities.PatientNote;
import com.cleverdev.testtask.entities.PatientProfile;
import com.cleverdev.testtask.entities.dao.PatientNoteDao;
import com.cleverdev.testtask.services.CompanyUserService;
import com.cleverdev.testtask.services.NoteUpdateService;

@Service
public class DefaultNoteUpdateService implements NoteUpdateService {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PatientNoteDao patientNoteDao;

    @Autowired
    private CompanyUserService companyUserService;

    @Override
    public boolean updateNoteIfNeeded(OldSystemNote oldNote, PatientProfile profile) {
        boolean isUpdated = false;
        PatientNote note = patientNoteDao.findByOldGuid(oldNote.getGuid()).orElse(new PatientNote());
        if (isNewOrEligibleForUpdate(oldNote, note)) {
            initDefaultsFields(oldNote, note);
            updateFields(oldNote, note, profile);
            patientNoteDao.save(note);
            isUpdated = true;
        }
        return isUpdated;
    }

    private void updateFields(OldSystemNote oldNote, PatientNote note, PatientProfile profile) {
        note.setNote(oldNote.getComments());
        note.setLastModifiedDateTime(LocalDateTime.parse(oldNote.getModifiedDateTime(), DATE_TIME_FORMATTER));
        note.setLastModifiedByUserId(companyUserService.createUserIfNotExist(oldNote.getLoggedUser()));
        note.setPatient(profile);
    }

    private boolean isNewOrEligibleForUpdate(OldSystemNote oldNote, PatientNote note) {
        return note.getLastModifiedDateTime() == null
                || note.getLastModifiedDateTime()
                        .isBefore(LocalDateTime.parse(oldNote.getModifiedDateTime(), DATE_TIME_FORMATTER));
    }

    private void initDefaultsFields(OldSystemNote oldNote, PatientNote note) {
        if (note.getOldGuid() == null) {
            note.setOldGuid(oldNote.getGuid());
            note.setCreatedDateTime(LocalDateTime.parse(oldNote.getCreatedDateTime(), DATE_TIME_FORMATTER));
            note.setCreatedByUserId(companyUserService.createUserIfNotExist(oldNote.getLoggedUser()));
        }
    }

    public void setPatientNoteDao(PatientNoteDao patientNoteDao) {
        this.patientNoteDao = patientNoteDao;
    }

    public void setCompanyUserService(CompanyUserService companyUserService) {
        this.companyUserService = companyUserService;
    }

}
