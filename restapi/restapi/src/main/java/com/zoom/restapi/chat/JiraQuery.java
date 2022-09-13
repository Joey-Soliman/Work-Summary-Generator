package com.zoom.restapi.chat;


import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Component
public class JiraQuery {

    @Autowired
    ZoomTokensRepository zoomTokensRepository;

    @Autowired
    JiraAuthorization jiraAuthorization;

    @Autowired
    JSON_Parser json_parser;

    private static HttpURLConnection connection;

    public String[][] getModifiedIssues(String id, int days) throws Exception {
        String jiraId = jiraAuthorization.getJiraId(id);
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        String jiraCloudId = zoomToken.getJiraCloudId();
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String accessToken = jiraAuthorization.getAccessToken(id);
        try {
            URL url = new URL("https://api.atlassian.com/ex/jira/" + jiraCloudId + "/rest/api/2/search?" +
                    "jql=status%20CHANGED%20DURING%20(startOfDay(-" + days + "d)%2C%20endOfDay())%20BY%20" + jiraId);
            connection = (HttpURLConnection) url.openConnection();

            //Request setup

            connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status == 401) {
                jiraAuthorization.authorizeRefreshToken(id);
                accessToken = jiraAuthorization.getAccessToken(id);
                connection.disconnect();
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization","Bearer " + accessToken);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                status = connection.getResponseCode();
            }

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.out.println("Contacts - Status: " + status + " - " + reader);
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line=reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }
        // issues - key
        // issues - fields - status - name
        // issues - fields - summary
        JSONObject jira = new JSONObject(responseContent.toString());
        JSONArray issuesJa = jira.getJSONArray("issues");
        String[][] recap = new String[issuesJa.length()][3];
        for (int i = 0; i < issuesJa.length(); i++) {
            JSONObject issue = issuesJa.getJSONObject(i);
            String key = issue.getString("key");
            JSONObject fields = issue.getJSONObject("fields");
            JSONObject status = fields.getJSONObject("status");
            String name = status.getString("name");
            String summary = fields.getString("summary");
            recap[i][0] = key;
            recap[i][1] = summary;
            recap[i][2] = name;
        }
        return recap;
    }

    public String[][] getCreatedIssues(String id, int days) throws Exception {
        String jiraId = jiraAuthorization.getJiraId(id);
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        String jiraCloudId = zoomToken.getJiraCloudId();
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String accessToken = jiraAuthorization.getAccessToken(id);
        try {
            URL url = new URL("https://api.atlassian.com/ex/jira/" + jiraCloudId + "/rest/api/2/search?" +
                    "jql=created%20>%3D%20startOfDay(-" + days +
                    "10d)%20and%20created%20<%3D%20endOfDay()%20and%20creator%20in%20(" + jiraId + ")");
            connection = (HttpURLConnection) url.openConnection();

            //Request setup

            connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status == 401) {
                jiraAuthorization.authorizeRefreshToken(id);
                accessToken = jiraAuthorization.getAccessToken(id);
                connection.disconnect();
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization","Bearer " + accessToken);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                status = connection.getResponseCode();
            }

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.out.println("Contacts - Status: " + status + " - " + reader);
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line=reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }
        // issues - key
        // issues - fields - status - name
        // issues - fields - summary
        JSONObject jira = new JSONObject(responseContent.toString());
        JSONArray issuesJa = jira.getJSONArray("issues");
        String[][] recap = new String[issuesJa.length()][3];
        for (int i = 0; i < issuesJa.length(); i++) {
            JSONObject issue = issuesJa.getJSONObject(i);
            String key = issue.getString("key");
            JSONObject fields = issue.getJSONObject("fields");
            JSONObject status = fields.getJSONObject("status");
            String name = status.getString("name");
            String summary = fields.getString("summary");
            recap[i][0] = key;
            recap[i][1] = summary;
            recap[i][2] = name;
        }
        return recap;
    }

    public String setCloudId(String id) throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String accessToken = jiraAuthorization.getAccessToken(id);
        try {
            URL url = new URL("https://api.atlassian.com/oauth/token/accessible-resources");
            connection = (HttpURLConnection) url.openConnection();

            //Request setup

            connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                System.out.println("Contacts - Status: " + status + " - " + reader);
                return "error";
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line=reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }
        JSONArray atlassianInfoList = new JSONArray(responseContent.toString());
        JSONObject jiraCloudInfo = (JSONObject) atlassianInfoList.get(0);
        String jiraCloudId = jiraCloudInfo.getString("id");

        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        zoomToken.setJiraCloudId(jiraCloudId);
        zoomTokensRepository.save(zoomToken);

        return "";
    }

}
