package com.example.backupservice.service;


import com.example.backupservice.config.AlfrescoConfig;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.springframework.stereotype.Service;

@Service
public class AlfrescoService {

    private final AlfrescoConfig alfrescoConfig;

    public AlfrescoService(AlfrescoConfig alfrescoConfig) {
        this.alfrescoConfig = alfrescoConfig;
    }


    public Document findByDocumentId(String documentId) {
        Document document = null;
        documentId = documentId.substring(0, documentId.indexOf(";"));
        try {
            document = (Document) alfrescoConfig.session.getObject(alfrescoConfig.session.createObjectId(documentId));
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }


        System.out.println("document = " + document);
        return document;
    }

}
