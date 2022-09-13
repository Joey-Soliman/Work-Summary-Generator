package com.zoom.restapi.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.zoom.restapi.chat.Authorization;
import com.zoom.restapi.chat.MySQL;
import com.zoom.restapi.chat.ZoomTokens;
import com.zoom.restapi.chat.ZoomTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


@RestController
public class ZoomOAuthController {

    @Autowired
    Authorization authorization;

    @RequestMapping(path = "/zoomOAuth", method = RequestMethod.GET)
    public String getCode(@RequestParam(name = "code") String code, HttpServletResponse response) throws Exception {
        authorization.authorizeAuthCode(code);


        String url = new GoogleAuthorizationCodeRequestUrl(
                "https://accounts.google.com/o/oauth2/auth",
                "1017761197758-aul61d78hi5p3v7tichn0mrb5h5s0fh3.apps.googleusercontent.com",
                "http://localhost:8080/GoogleOAuth",
                Arrays.asList("https://www.googleapis.com/auth/documents", "https://www.googleapis.com/auth/drive",
                        "https://www.googleapis.com/auth/calendar"))
                .setAccessType("offline").build();
        response.sendRedirect(url);
        return "";
    }
}