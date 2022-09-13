package com.zoom.restapi.chat;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class GoogleDocs {

    @Autowired
    GoogleAuthorization googleAuthorization;

    public String createDoc(String id) throws IOException, GeneralSecurityException {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = googleAuthorization.getCredential(id);
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("ZoomChat")
                .build();

        File body = new File();
        body.setTitle("ZoomChat").setDescription("Messages").setMimeType("text/plain");
        java.io.File fileContent = new java.io.File("Messages.txt");
        FileContent mediaContent = new FileContent("text/plain", fileContent);
        File file = service.files().insert(body, mediaContent).execute();
        googleAuthorization.setAccessToken(id, credential.getAccessToken());
        return file.getId();
    }

}
