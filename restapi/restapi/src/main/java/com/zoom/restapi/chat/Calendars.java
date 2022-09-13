package com.zoom.restapi.chat;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;


@Component
public class Calendars {

    @Autowired
    GoogleAuthorization googleAuthorization;

    public String getTimeZone(String id) throws GeneralSecurityException, IOException {
        String timeZone = "";
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = googleAuthorization.getCredential(id);
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("applicationName").build();
        CalendarList calendarList = service.calendarList().list().execute();
        List<CalendarListEntry> items = calendarList.getItems();
        CalendarListEntry calendarListEntry = items.get(0);
        timeZone = calendarListEntry.getTimeZone();
        return timeZone;
    }

    public String getEvents(String id, int days) throws GeneralSecurityException, IOException {
        // Date format
        SimpleDateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        // SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        // Current time on machine
        long epoch = System.currentTimeMillis();
        // System.out.println(df.format(epoch));
        // Find time of day
        long timeOfDay = (long) (epoch % 8.64e7);
        // System.out.println(time.format(timeOfDay));
        // Offset by calendar timezone
        String timeZone = getTimeZone(id);
        long timeShift = getTimeZoneDif(timeZone);
        epoch += timeShift;
        // System.out.println(df.format(epoch));
        // Get 00:00:00 am and 11:59:59pm
        long timeMin = epoch - timeOfDay;
        long timeMax = epoch - timeOfDay - 1;
        // Convert to UTC
        timeMin += (long) (2.52e7);
        timeMax += (long) (2.52e7);
        // Offset by days
        timeMin -= (long) (8.64e7 * days);
        // System.out.println(df.format(timeMin));
        // System.out.println(df.format(timeMax));
        // Convert to Google DateTime
        DateTime min = new DateTime(timeMin);
        DateTime max = new DateTime(timeMax);

        // Get Calendar Events
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = googleAuthorization.getCredential(id);
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("applicationName").build();
        String pageToken = null;
        String calendarEvents = "";
        do {
            Events events = service.events().list("primary").setPageToken(pageToken).setTimeMax(max)
                    .setTimeMin(min).setOrderBy("startTime").setSingleEvents(true).execute();
            List<Event> items = events.getItems();
            for (Event event : items) {
                long start = event.getStart().getDateTime().getValue();
                Date startDate = new Date(start);
                calendarEvents += df.format(startDate) + ": " + event.getSummary() + System.lineSeparator();
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
        return calendarEvents;
    }

    public long getTimeZoneDif(String timeZone) {
        long timeShift = ChronoUnit.MILLIS.between(LocalDateTime.now(ZoneId.of(timeZone)), LocalDateTime.now());
        // System.out.println("timeShift: " + timeShift);
        return timeShift;
    }

}
