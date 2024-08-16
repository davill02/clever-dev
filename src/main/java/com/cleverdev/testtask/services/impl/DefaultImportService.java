package com.cleverdev.testtask.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.cleverdev.testtask.dto.NoteRequest;
import com.cleverdev.testtask.dto.OldSystemClientInfo;
import com.cleverdev.testtask.dto.OldSystemNote;
import com.cleverdev.testtask.entities.PatientProfile;
import com.cleverdev.testtask.services.FilterService;
import com.cleverdev.testtask.services.ImportService;
import com.cleverdev.testtask.services.NoteUpdateService;

@Service
public class DefaultImportService implements ImportService {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultImportService.class);

    private final static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RestClient restClient;

    @Autowired
    private FilterService filterService;

    @Autowired
    private NoteUpdateService noteUpdateService;

    @Value("${post.all.clients}")
    private String postAllClientsUri;

    @Value("${post.all.notes.for.client}")
    private String postAllNotesForClientsUri;

    @Override
    @Scheduled(cron = " 00 15 */2 * * * ")
    public void importData() {
        List<OldSystemClientInfo> oldPatients = collectAllPatients();
        List<Map.Entry<OldSystemClientInfo, PatientProfile>> filteredPatients = filterService
                .clientToUpdate(oldPatients);
        LOG.info("Patients to update notes: {}", filteredPatients.size());
        importNotes(filteredPatients);
    }

    private List<OldSystemClientInfo> collectAllPatients() {
        try {
            return restClient.post().uri(postAllClientsUri)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<OldSystemClientInfo>>() {
                    }).getBody();
        } catch (Exception e) {
            LOG.error("Unable to connect to {} ", postAllClientsUri, e);
            return Collections.emptyList();
        }

    }

    private void importNotes(List<Map.Entry<OldSystemClientInfo, PatientProfile>> filteredPatients) {

        for (Map.Entry<OldSystemClientInfo, PatientProfile> entry : filteredPatients) {

            NoteRequest noteRequest = getNoteRequest(entry);

            List<OldSystemNote> notes = getNotes(noteRequest);

            updateNotes(notes, entry.getValue());

        }
    }

    private List<OldSystemNote> getNotes(NoteRequest noteRequest) {
        List<OldSystemNote> notes = Collections.emptyList();
        try {
            notes = restClient.post().uri(postAllNotesForClientsUri)
                    .contentType(MediaType.APPLICATION_JSON).body(noteRequest).retrieve()
                    .toEntity(new ParameterizedTypeReference<List<OldSystemNote>>() {
                    }).getBody();
        } catch (Exception e) {
            LOG.info("Unable to connect to {}, for guid: {}, agency: {} due to {}", postAllNotesForClientsUri,
                    noteRequest.getClientGuid(), noteRequest.getAgency(), e.getMessage());
            LOG.debug("Additional info about {} {}", noteRequest.getClientGuid(), noteRequest.getAgency(), e);
        }
        return notes;
    }

    private NoteRequest getNoteRequest(Map.Entry<OldSystemClientInfo, PatientProfile> entry) {
        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setAgency(entry.getKey().getAgency());
        noteRequest.setClientGuid(entry.getKey().getGuid());
        noteRequest.setDateTo(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        noteRequest.setDateFrom(LocalDate.parse(entry.getKey().getCreatedDateTime(), DEFAULT_DATE_FORMATTER)
                .format(DateTimeFormatter.ISO_LOCAL_DATE));
        return noteRequest;
    }

    private void updateNotes(List<OldSystemNote> notes, PatientProfile patientProfile) {
        LOG.info("Updating {} notes for patient {}", notes.size(), patientProfile.getId());
        int updated = 0;
        for (OldSystemNote note : notes) {
            if (noteUpdateService.updateNoteIfNeeded(note, patientProfile)) {
                updated++;
            }
        }
        LOG.info("Updated {} notes, {} skipped for patient {}", updated, (notes.size() - updated),
                patientProfile.getId());
    }

}
