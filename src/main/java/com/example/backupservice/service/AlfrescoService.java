package com.example.backupservice.service;

import org.apache.chemistry.opencmis.client.api.Document;

public interface AlfrescoService {

    Document findByDocumentId(String documentId);
}
