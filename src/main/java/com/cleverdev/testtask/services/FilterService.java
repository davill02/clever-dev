package com.cleverdev.testtask.services;

import java.util.List;
import java.util.Map;

import com.cleverdev.testtask.dto.OldSystemClientInfo;
import com.cleverdev.testtask.entities.PatientProfile;

public interface FilterService {
    List<Map.Entry<OldSystemClientInfo, PatientProfile>> clientToUpdate(List<OldSystemClientInfo> clientInfos);
}
