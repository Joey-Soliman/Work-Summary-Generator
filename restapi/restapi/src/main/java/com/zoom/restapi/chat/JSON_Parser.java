package com.zoom.restapi.chat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.springframework.stereotype.Component;


@Component
public class JSON_Parser {

    public static String parseInner(String json, String type) throws Exception {
        // parsing file "JSONExample.json"
        String innerList = "";
        Object obj = new JSONParser().parse(json);

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // get email
        innerList = (String) jo.get(type);
        return innerList;
    }

    public static String parseInner2(String json, String type1, String type2) throws Exception {
        // parsing file "JSONExample.json"
        String innerList = "";
        Object obj = new JSONParser().parse(json);

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // get sender: message
        innerList = "    " + (String) jo.get(type2) + ": " + (String) jo.get(type1);
        return innerList;
    }

    public static ArrayList<String> parseOuter(String json, String type1, String type2, String type3, String email, LocalDate date) throws Exception {
        // parsing file "JSONExample.json"
        ArrayList<String> outerList = new ArrayList<String>();

        // check if access token still good
        if (json == "error") {
            return outerList;
        }

        Object obj = new JSONParser().parse(json);
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // get contacts array
        JSONArray jsonArray = (JSONArray) jo.get(type1);

        // get json object contact
        Iterator<JSONObject> iterator = jsonArray.iterator();

        // parsing contacts
        if (type1 == "contacts") {
            while(iterator.hasNext()) {
                outerList.add(parseInner(iterator.next().toString(), type2));
            }
        }
        // parsing messages
        else {
            while(iterator.hasNext()) {
                outerList.add(parseInner2(iterator.next().toString(), type2, type3));
            }
        }
        return outerList;
    }

}
