package com.cleverdev.testtask.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.cleverdev.testtask.dto.OldSystemClientInfo;
import com.cleverdev.testtask.entities.PatientProfile;
import com.cleverdev.testtask.entities.dao.PatientProfileDao;

public class FilterServiceTest {

    @Mock
    PatientProfileDao patientProfileDao;

    OldSystemClientInfo first = new OldSystemClientInfo();
    OldSystemClientInfo second = new OldSystemClientInfo();
    OldSystemClientInfo third = new OldSystemClientInfo();

    PatientProfile firstP = new PatientProfile();
    PatientProfile secondP = new PatientProfile();

    DefaultFilterService defaultFilterService = new DefaultFilterService();

    List<OldSystemClientInfo> clientInfos;

    @BeforeEach
    void setUp() {
        patientProfileDao = mock(PatientProfileDao.class);
        defaultFilterService.setPatientProfileDao(patientProfileDao);
        clientInfos = List.of(first, second, third);
        first.setGuid("FIRST");
        second.setGuid("SECOND");
        third.setGuid("THIRD");
        firstP.setOldClientGuid("FIRST");
        secondP.setOldClientGuid("SECOND");
    }

    @Test
    void testClientToUpdate() {
        when(patientProfileDao.getProfileIfActive(eq("%" + first.getGuid() + "%"))).thenReturn(Optional.of(firstP));
        when(patientProfileDao.getProfileIfActive(eq("%" + second.getGuid() + "%"))).thenReturn(Optional.of(secondP));
        when(patientProfileDao.getProfileIfActive(eq("%" + third.getGuid() + "%"))).thenReturn(Optional.empty());

        var result = defaultFilterService.clientToUpdate(clientInfos);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), Map.entry(first, firstP));
        assertEquals(result.get(1), Map.entry(second, secondP));
    }
}
