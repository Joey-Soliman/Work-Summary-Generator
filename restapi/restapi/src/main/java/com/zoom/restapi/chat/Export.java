package com.zoom.restapi.chat;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Export {

    public static void exportArrayList(ArrayList<String> messageList) throws IOException {
        String message;
        try {
            FileWriter myWriter = new FileWriter("Messages.txt", true);
            Iterator messageIterator = messageList.iterator();
            while (messageIterator.hasNext()) {
                message = (String) messageIterator.next();
                myWriter.write(message);
                myWriter.write(System.getProperty("line.separator"));
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing messages to file.");
            e.printStackTrace();
        }
    }

    public static void exportString(String string) throws IOException {
        try {
            FileWriter myWriter = new FileWriter("Messages.txt", true);
            myWriter.write(string);
            myWriter.write(System.getProperty("line.separator"));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing email/date to file.");
            e.printStackTrace();
        }
    }

    public static void clearFile() throws IOException {
        try {
            FileWriter myWriter = new FileWriter("Messages.txt");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while clearing file.");
            e.printStackTrace();
        }
    }
}
