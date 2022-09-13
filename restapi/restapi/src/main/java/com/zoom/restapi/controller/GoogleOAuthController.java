package com.zoom.restapi.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.zoom.restapi.chat.Authorization;
import com.zoom.restapi.chat.GoogleAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
public class GoogleOAuthController {

    @Autowired
    GoogleAuthorization googleAuthorization;

    @RequestMapping(path = "/GoogleOAuth", method = RequestMethod.GET)
    public String getCode(@RequestParam(name = "code") String code, HttpServletResponse response) throws Exception {
        googleAuthorization.authorizeAuthCode(code);


        String client_id = "HHSN650wpd5F9AGQjpjg0WRWXnlnASDZ";
        String scope = "read:jira-work%20offline_access%20read:me";
        String redirect_uri = "http://localhost:8080/jiraOAuth";
        String state = "2";

        String url = "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=" +
                client_id + "&scope=" + scope + "&redirect_uri=" + redirect_uri + "&state=" + state +
                "&response_type=code&prompt=consent";
        response.sendRedirect(url);
        return "";
    }
}
