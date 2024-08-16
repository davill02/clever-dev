package com.cleverdev.testtask.services;

import com.cleverdev.testtask.dto.OldSystemNote;
import com.cleverdev.testtask.entities.PatientProfile;

public interface NoteUpdateService {
    boolean updateNoteIfNeeded(OldSystemNote oldNote, PatientProfile patientProfile);
}
