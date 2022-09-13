package com.zoom.restapi.controller;

import com.zoom.restapi.chat.JiraAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JiraOAuthController {

    @Autowired
    JiraAuthorization jiraAuthorization;

    @RequestMapping(path = "/jiraOAuth", method = RequestMethod.GET)
    public String getCode(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) throws Exception {
        jiraAuthorization.authorizeAuthCode(code);
        return "";
    }

}
