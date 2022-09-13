package com.zoom.restapi.chat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "zoom_auth")
public class ZoomTokens {
    @Id
    private String id;
    private String access_token;
    private String refresh_token;
    private String google_access_token;
    private String google_refresh_token;
    private String jira_access_token;
    private String jira_refresh_token;
    private String jira_id;
    private String jira_cloud_id;

    public ZoomTokens() {}

    public ZoomTokens(String id, String access_token, String refresh_token) {
        this.id = id;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public void setGoogleTokens(String access_token, String refresh_token) {
        this.google_access_token = access_token;
        this.google_refresh_token = refresh_token;
    }

    public void setGoogleAccessToken(String google_access_token) {
        this.google_access_token = google_access_token;
    }

    public void setJiraTokens(String access_token, String refresh_token) {
        this.jira_access_token = access_token;
        this.jira_refresh_token = refresh_token;
    }

    public void setJiraId(String jira_id) {
        this.jira_id = jira_id;
    }

    public void setJiraCloudId(String jira_cloud_id) { this.jira_cloud_id = jira_cloud_id; }

    public String getRefreshToken() {
        return refresh_token;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getGoogleRefreshToken() {
        return google_refresh_token;
    }

    public String getGoogleAccessToken() {
        return google_access_token;
    }

    public String getJiraAccessToken() { return jira_access_token; }

    public String getJiraRefreshToken() { return jira_refresh_token; }

    public String getJiraId() { return jira_id; }

    public String getJiraCloudId() { return jira_cloud_id; }



    @Override
    public String toString() {
        return "ZoomTokens{" + "id=" + id + ", access_token=" + access_token + ", refresh_token=" + refresh_token +
                ", google_access_token=" + google_access_token + ", google_refresh_token=" + google_refresh_token +
                ", jira_access_token=" + jira_access_token + "jira_refresh_token" + jira_refresh_token + "jira_id" +
                jira_id + "jira_cloud_id" + jira_cloud_id + "}";
    }
}
