package com.example.backupservice.service.impl;


import com.example.backupservice.config.AlfrescoConfig;
import com.example.backupservice.service.AlfrescoService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.stereotype.Service;

@Service
public class AlfrescoServiceImpl implements AlfrescoService {

    private final AlfrescoConfig alfrescoConfig;

    public AlfrescoServiceImpl(AlfrescoConfig alfrescoConfig) {
        this.alfrescoConfig = alfrescoConfig;
    }


    @Override
    public Document findByDocumentId(String documentId) {
        Document document = null;
        documentId = documentId.substring(0, documentId.indexOf(";"));
        try {
            document = (Document) alfrescoConfig.session.getObject(alfrescoConfig.session.createObjectId(documentId));
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

        return document;
    }

}
