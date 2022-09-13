package com.zoom.restapi.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

@Component
public class JiraAuthorization {

    @Autowired
    ZoomTokensRepository zoomTokensRepository;

    @Autowired
    Authorization authorization;

    @Autowired
    JiraQuery jiraQuery;

    public void authorizeAuthCode(String authCode) throws Exception {
        URL url = new URL("https://auth.atlassian.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        String jsonInputString = "{\"grant_type\": \"authorization_code\",\"client_id\": \"HHSN650wpd5F9AGQjpjg0WRWXnlnASDZ\"," +
                "\"client_secret\": \"eRo436mNvPkXTz5D0DVyWtZX3Qws1s9SxmfSeLDWZHdGagam53EiXODool1DSB9z\",\"code\": \"" + authCode + "\"," +
                "\"redirect_uri\": \"http://localhost:8080/jiraOAuth\"}";
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String accessToken = JSON_Parser.parseInner (response.toString(), "access_token");
            String refreshToken = JSON_Parser.parseInner (response.toString(), "refresh_token");
            String id = authorization.getUserId();
            Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
            ZoomTokens zoomToken = zoomTokens.get();
            zoomToken.setJiraTokens(accessToken, refreshToken);
            zoomTokensRepository.save(zoomToken);
            setJiraId(accessToken, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }

        String id = authorization.getUserId();
        jiraQuery.setCloudId(id);
    }


    public void authorizeRefreshToken(String id) throws IOException {
        String refreshToken = getRefreshToken(id);
        URL url = new URL("https://auth.atlassian.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        String jsonInputString = "{\"grant_type\": \"refresh_token\",\"client_id\": \"HHSN650wpd5F9AGQjpjg0WRWXnlnASDZ\"," +
                "\"client_secret\": \"eRo436mNvPkXTz5D0DVyWtZX3Qws1s9SxmfSeLDWZHdGagam53EiXODool1DSB9z\",\"refresh_token\": \"" +
                refreshToken + "}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String accessToken = JSON_Parser.parseInner (response.toString(), "access_token");
            String refreshTokenNew = JSON_Parser.parseInner (response.toString(), "refresh_token");
            Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
            ZoomTokens zoomToken = zoomTokens.get();
            zoomToken.setJiraTokens(accessToken, refreshTokenNew);
            zoomTokensRepository.save(zoomToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setJiraId(String accessToken, String id) throws Exception {
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String jiraId = JSON_Parser.parseInner(payload, "sub");
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        zoomToken.setJiraId(jiraId);
        zoomTokensRepository.save(zoomToken);
    }


    public String getJiraId(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        return zoomToken.getJiraId();
    }


    public String getAccessToken(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        return zoomToken.getJiraAccessToken();
    }


    public String getRefreshToken(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        return zoomToken.getJiraRefreshToken();
    }
}
