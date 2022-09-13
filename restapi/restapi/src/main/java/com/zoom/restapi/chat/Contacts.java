package com.zoom.restapi.chat;

import com.zoom.restapi.controller.ZoomOAuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class Contacts {

    @Autowired
    Authorization authorization;

    private static HttpURLConnection connection;

    public String getContacts(String id) throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String accessToken = authorization.getAccessToken(id);
        try {
            URL url = new URL("https://api.zoom.us/v2/chat/users/me/contacts?type=external");
            connection = (HttpURLConnection) url.openConnection();

            //Request setup

            connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status == 401) {
                authorization.authorizeRefreshToken(id);
                accessToken = authorization.getAccessToken(id);
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
                System.out.println("Contacts: " + reader);
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
        return responseContent.toString();
    }
}
