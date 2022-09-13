package com.zoom.restapi.controller;

import com.zoom.restapi.chat.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

@RestController
public class ZoomMessageController {
    @Autowired
    Contacts contacts;

    @Autowired
    Messages messagesClass;

    @Autowired
    Authorization authorization;

    @Autowired
    Calendars calendars;

    @Autowired
    GoogleDocs googleDocs;

    @Autowired
    JiraQuery jiraQuery;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public void defaultEndpoint() {
        return;
    }


    @RequestMapping(path = "/messages", method = RequestMethod.GET)
    public String getMessages(@RequestParam(name = "days") String length, @RequestParam(name = "id") String id) throws Exception {
        System.out.println("Received messages endpoint. Days = " + length);
        int days = Integer.parseInt(length);
        String recap = "";
        String email;
        String message;
        ArrayList<String> messages = new ArrayList<>();
        // Date format
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM.dd.yyyy");
        // Set date to UTC time
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        // Get Calendar Events
        recap += "Calendar Events:" + System.lineSeparator();
        recap += calendars.getEvents(id, days);
        // Get Jira issues modified
        String[][] jiraM = jiraQuery.getModifiedIssues(id, days);
        String[] keys = new String[jiraM.length];
        String updated = "";
        for (int i = 0; i < jiraM.length; i++) {
            keys[i] = jiraM[i][0];
            updated += jiraM[i][0] + ": " + jiraM[i][1] + " - " + jiraM[i][2] + System.lineSeparator();
        }
        // Get Jira issues created and add to recap
        String[][] jiraC = jiraQuery.getCreatedIssues(id, days);
        if (jiraC.length > 0)
            recap += "Jira Issues Created:" + System.lineSeparator();
        for (int i = 0; i < jiraC.length; i++) {
            if (!search(keys, jiraC[i][0])) {
                recap += jiraC[i][0] + ": " + jiraC[i][1] + " - " + jiraC[i][2] + System.lineSeparator();
            }
        }
        // Add the modified issues
        recap += "Jira Issues Status Updated:" + System.lineSeparator();
        recap += updated;
        /*
        // Get emails in contacts list
        ArrayList<String> emailList= JSON_Parser.parseOuter(contacts.getContacts(id), "contacts", "email", "", "", today);
        Iterator emailIterator = emailList.iterator();
        while (emailIterator.hasNext()) {
            email = (String) emailIterator.next();
            for (int i = days - 1; i > -1; i--) {
                // Clears messages arraylist when moving on to next date
                messages.clear();
                LocalDate date = today.minusDays(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                // Get sender: message from messages
                ArrayList<String> messageList = JSON_Parser.parseOuter(messagesClass.getMessages(email, date, id), "messages", "message", "sender", email, date);
                Iterator messageIterator = messageList.iterator();
                if (messageIterator.hasNext()) {
                    recap += "Chats from " + email + ":" + System.lineSeparator();
                }
                while (messageIterator.hasNext()) {
                    message = (String) messageIterator.next();
                    messages.add(message);
                }
                Collections.reverse(messages);
                Iterator messageIterator2 = messages.iterator();
                while (messageIterator2.hasNext()) {
                    recap += date.format(df) + ":" + System.lineSeparator();
                    message = (String) messageIterator2.next();
                    recap += message + System.lineSeparator();
                }
            }
        }

         */

        // Print to file
        Export.clearFile();
        Export.exportString(recap);
        // Export to googleDrive
        // googleDocs.createDoc(id);
        System.out.println("Done");
        return "Done";
    }

    public Boolean search(String[] keys, String searchKey) {
        for (String key : keys) {
            if (key.equals(searchKey))
                return true;
        }
        return false;
    }
}
