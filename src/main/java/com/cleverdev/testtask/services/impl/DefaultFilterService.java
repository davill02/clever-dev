package com.cleverdev.testtask.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleverdev.testtask.dto.OldSystemClientInfo;
import com.cleverdev.testtask.entities.PatientProfile;
import com.cleverdev.testtask.entities.dao.PatientProfileDao;
import com.cleverdev.testtask.services.FilterService;

@Service
public class DefaultFilterService implements FilterService {

    @Autowired
    private PatientProfileDao patientProfileDao;

    public List<Map.Entry<OldSystemClientInfo, PatientProfile>> clientToUpdate(List<OldSystemClientInfo> clientInfos) {
        return clientInfos.stream()
                .map(clientInfo -> Map.entry(clientInfo,
                        patientProfileDao.getProfileIfActive('%' + clientInfo.getGuid() + '%')))
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
                .collect(Collectors.toList());
    }

    public void setPatientProfileDao(PatientProfileDao patientProfileDao) {
        this.patientProfileDao = patientProfileDao;
    }

}
