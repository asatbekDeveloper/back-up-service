package com.example.backupservice.config;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class AlfrescoConfig {

    @Value("${alfresco.repository.url}")
    private String alfrescoUrl;

    @Value("${alfresco.repository.user}")
    private String alfrescoUser;

    @Value("${alfresco.repository.password}")
    private String alfrescoPassword;

    @Value("${base.folder}")
    public String baseFolder;

    public Session session;

    @PostConstruct
    public void init() {

        String alfrescoBrowserUrl = alfrescoUrl + "/api/-default-/public/cmis/versions/1.1/browser";

        Map<String, String> parameter = new HashMap<>();

        parameter.put(SessionParameter.USER, alfrescoUser);
        parameter.put(SessionParameter.PASSWORD, alfrescoPassword);

        parameter.put(SessionParameter.BROWSER_URL, alfrescoBrowserUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

        SessionFactory factory = SessionFactoryImpl.newInstance();
        session = factory.getRepositories(parameter).get(0).createSession();

    }

    public Folder getRootFolder() {
        return session.getRootFolder();
    }

    public Folder getEgpFolder() {
        for (CmisObject child : session.getRootFolder().getChildren()) {
            if (child instanceof Folder && child.getName().equals(baseFolder)) return (Folder) child;
        }
        throw new RuntimeException("Base folder not found");
    }

}


