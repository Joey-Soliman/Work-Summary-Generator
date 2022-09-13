package com.zoom.restapi.chat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Authorization {

    @Autowired
    ZoomTokensRepository zoomTokensRepository;

    private static HttpURLConnection connection;
    private static String accessToken = "";
    private static String refreshToken = "";
    private static String id = "";

    public static String getUserId() throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL("https://api.zoom.us/v2/users/me");
            connection = (HttpURLConnection) url.openConnection();

            //Request setup
            connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
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
        return JSON_Parser.parseInner (responseContent.toString(), "email");
    }

    public void authorizeRefreshToken(String id) throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        getRefreshToken(id);
        try {
            URL url = new URL("https://zoom.us/oauth/token?grant_type=refresh_token&refresh_token=" + refreshToken);
            connection = (HttpURLConnection) url.openConnection();

            //Request setup
            connection.setRequestProperty("Authorization","Basic aDJ1ZEJ5b1BUZDJEal9kV0F4dFpYZzp5VWdOVnM1RXlRbFdYQ3Y3UUlTaU9ucVYzVUZ0MmZPNQ==");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
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
        accessToken = JSON_Parser.parseInner (responseContent.toString(), "access_token");
        refreshToken = JSON_Parser.parseInner (responseContent.toString(), "refresh_token");
        zoomTokensRepository.deleteById(id);
        writeTokens(id, accessToken, refreshToken);
    }

    public String authorizeAuthCode(String authCode) throws Exception {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL("https://zoom.us/oauth/token?grant_type=authorization_code&code=" + authCode + "&redirect_uri=http://localhost:8080/zoomOAuth");
            connection = (HttpURLConnection) url.openConnection();

            //Request setup
            connection.setRequestProperty("Authorization","Basic aDJ1ZEJ5b1BUZDJEal9kV0F4dFpYZzp5VWdOVnM1RXlRbFdYQ3Y3UUlTaU9ucVYzVUZ0MmZPNQ==");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
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
        accessToken = JSON_Parser.parseInner (responseContent.toString(), "access_token");
        refreshToken = JSON_Parser.parseInner (responseContent.toString(), "refresh_token");
        id = getUserId();
        writeTokens(id, accessToken, refreshToken);
        return accessToken;
    }


    public String getRefreshToken(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        refreshToken = zoomToken.getRefreshToken();
        return refreshToken;
    }

    public String getAccessToken(String id) {
        Optional<ZoomTokens> zoomTokens = zoomTokensRepository.findById(id);
        ZoomTokens zoomToken = zoomTokens.get();
        accessToken = zoomToken.getAccessToken();
        return accessToken;
    }

    
    private void writeTokens(String id, String accessToken, String refreshToken) throws Exception{
        zoomTokensRepository.save(new ZoomTokens(id, accessToken, refreshToken));
    }

}
