package com.cleverdev.testtask.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.cleverdev.testtask.dto.OldSystemNote;
import com.cleverdev.testtask.entities.CompanyUser;
import com.cleverdev.testtask.entities.PatientNote;
import com.cleverdev.testtask.entities.PatientProfile;
import com.cleverdev.testtask.entities.dao.PatientNoteDao;
import com.cleverdev.testtask.services.CompanyUserService;

public class DefaultNoteUpdateServiceTest {

    private static final String NEW_VALUE = "NEW_VALUE";

    private static final String OLD_VALUE = "OLD_VALUE";

    private static final String LOGGED_USER = "LOGGED_USER";

    private static final String OLD_GUID_NOT_EXIST_IN_CURRENT = "OLD_GUID_NOT_EXIST_IN_CURRENT";

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String OLD_GUID = "OLD_GUID";

    DefaultNoteUpdateService defaultNoteUpdateService = new DefaultNoteUpdateService();

    @Mock
    CompanyUserService companyUserService;

    @Mock
    PatientNoteDao patientNoteDao;

    PatientNote notExistedPatientNote = new PatientNote();

    PatientNote existedPatientNote = new PatientNote();

    PatientProfile profile = new PatientProfile();

    OldSystemNote notExistedNote = new OldSystemNote();

    OldSystemNote existedNote = new OldSystemNote();

    CompanyUser loggedUser = new CompanyUser();

    LocalDateTime creationDate = LocalDateTime.now();

    LocalDateTime lastModificationDateTime = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        companyUserService = mock(CompanyUserService.class);
        patientNoteDao = mock(PatientNoteDao.class);
        defaultNoteUpdateService.setCompanyUserService(companyUserService);
        defaultNoteUpdateService.setPatientNoteDao(patientNoteDao);

        creationDate = LocalDateTime.parse(creationDate.format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
        lastModificationDateTime = LocalDateTime.parse(lastModificationDateTime.format(DATE_TIME_FORMATTER),
                DATE_TIME_FORMATTER);

        notExistedNote.setGuid(OLD_GUID_NOT_EXIST_IN_CURRENT);
        notExistedNote.setLoggedUser(LOGGED_USER);
        notExistedNote.setComments(OLD_VALUE);
        notExistedNote.setCreatedDateTime(creationDate.format(DATE_TIME_FORMATTER));
        notExistedNote.setModifiedDateTime(creationDate.format(DATE_TIME_FORMATTER));

        existedNote.setGuid(OLD_GUID);
        existedNote.setLoggedUser(LOGGED_USER);
        existedNote.setComments(NEW_VALUE);
        existedNote.setCreatedDateTime(creationDate.format(DATE_TIME_FORMATTER));
        existedNote.setModifiedDateTime(lastModificationDateTime.format(DATE_TIME_FORMATTER));

        existedPatientNote.setOldGuid(OLD_GUID);
        existedPatientNote.setNote(OLD_VALUE);

        loggedUser.setLogin(LOGGED_USER);
    }

    @Test
    void testCreateNoteIfNeeded() {
        when(patientNoteDao.findByOldGuid(eq(OLD_GUID_NOT_EXIST_IN_CURRENT)))
                .thenReturn(Optional.of(notExistedPatientNote));
        when(companyUserService.createUserIfNotExist(eq(LOGGED_USER))).thenReturn(loggedUser);

        defaultNoteUpdateService.updateNoteIfNeeded(notExistedNote, profile);

        assertEquals(notExistedPatientNote.getCreatedByUserId(), loggedUser);
        assertEquals(notExistedPatientNote.getOldGuid(), OLD_GUID_NOT_EXIST_IN_CURRENT);
        assertEquals(notExistedPatientNote.getNote(), OLD_VALUE);
        assertEquals(notExistedPatientNote.getCreatedDateTime(), creationDate);
        assertEquals(notExistedPatientNote.getLastModifiedDateTime(), creationDate);
        verify(patientNoteDao, times(1)).save(any());
    }

    @Test
    void testUpdateNoteIfNeeded() {
        when(patientNoteDao.findByOldGuid(eq(OLD_GUID)))
                .thenReturn(Optional.of(existedPatientNote));
        when(companyUserService.createUserIfNotExist(eq(LOGGED_USER))).thenReturn(loggedUser);

        defaultNoteUpdateService.updateNoteIfNeeded(existedNote, profile);

        assertEquals(existedPatientNote.getLastModifiedByUserId(), loggedUser);
        assertEquals(existedPatientNote.getCreatedByUserId(), null);
        assertEquals(existedPatientNote.getOldGuid(), OLD_GUID);
        assertEquals(existedPatientNote.getNote(), NEW_VALUE);
        assertEquals(existedPatientNote.getCreatedDateTime(), null);
        assertEquals(existedPatientNote.getLastModifiedDateTime(), lastModificationDateTime);
        verify(patientNoteDao, times(1)).save(any());
    }

    @Test
    void testNotUpdateNoteIfNeeded() {
        existedNote.setModifiedDateTime(lastModificationDateTime.minusMonths(12).format(DATE_TIME_FORMATTER));
        existedPatientNote.setLastModifiedDateTime(lastModificationDateTime);
        when(patientNoteDao.findByOldGuid(eq(OLD_GUID)))
                .thenReturn(Optional.of(existedPatientNote));
        when(companyUserService.createUserIfNotExist(eq(LOGGED_USER))).thenReturn(loggedUser);

        defaultNoteUpdateService.updateNoteIfNeeded(existedNote, profile);

        verify(patientNoteDao, never()).save(any());
    }
}
