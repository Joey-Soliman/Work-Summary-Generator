package com.zoom.restapi.chat;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleAuthorization {

    @Autowired
    ZoomTokensRepository zoomTokensRepository;

    @Autowired
    Authorization authorization;

    private static String accessToken = "";
    private static String refreshToken = "";
    private static GoogleCredential credential;
    private static String id;

    public void authorizeAuthCode(String authCode) throws Exception {
        try {
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), new GsonFactory(),
                    "1017761197758-aul61d78hi5p3v7tichn0mrb5h5s0fh3.apps.googleusercontent.com", "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",
                    authCode, "http://localhost:8080/GoogleOAuth")
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token?access_type=offline"))
                    .execute();
            accessToken = response.getAccessToken();
            refreshToken = response.getRefreshToken();
            id = authorization.getUserId();
            Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
            ZoomTokens zoomToken = zoomTokens.get();
            zoomToken.setGoogleTokens(accessToken, refreshToken);
            zoomTokensRepository.save(zoomToken);
            JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            credential = new GoogleCredential.Builder()
                    .setClientSecrets("1017761197758-aul61d78hi5p3v7tichn0mrb5h5s0fh3.apps.googleusercontent.com",
                            "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")
                    .setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT)
                    .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                    .build().setAccessToken(accessToken).setRefreshToken(refreshToken);
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

    public void authorizeRefreshToken(String id) throws Exception {
        getRefreshToken(id);
        credential.setRefreshToken(id);
        credential.refreshToken();
        accessToken = credential.getAccessToken();
        refreshToken = credential.getRefreshToken();
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        zoomToken.setGoogleTokens(accessToken, refreshToken);
    }

    public String getRefreshToken(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        refreshToken = zoomToken.getGoogleRefreshToken();
        return refreshToken;
    }

    public GoogleCredential getCredential(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        accessToken = zoomToken.getGoogleAccessToken();
        credential.setAccessToken(accessToken);
        return credential;
    }

    /*
    public void setRefreshToken(String id, String refreshToken) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        zoomToken.setGoogleRefreshToken(refreshToken);
        zoomTokensRepository.save(zoomToken);
    }
     */

    public void setAccessToken(String id, String accessToken) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        zoomToken.setGoogleAccessToken(accessToken);
        zoomTokensRepository.save(zoomToken);
    }
}
