// Comment Generator generates comments according to parameters

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class CommentGenerator implements Runnable {
    public DataOutputStream outputToServer;
    public int[] parameters;
    public String autoCommentStart;

    CommentGenerator(DataOutputStream outputToServer, int[] parameters, String starter) {
        this.outputToServer = outputToServer;
        this.parameters = parameters;
        this.autoCommentStart = starter;
    }

    public void run() {
        // Generate comments.
        try {
            while (true) {
                if (this.outputToServer == null) {
                    break;
                }
                int autoCommentLength = this.parameters[0]; // length
                int autoCommentInterval = this.parameters[1]; // interval

                sendAutoComment(this.autoCommentStart, autoCommentLength, this.outputToServer);
                Thread.sleep(autoCommentInterval); // sleep  
            } // while
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public void sendAutoComment(String start, int length, DataOutputStream outputToServer) {
        String randomStr = "";
        for (int i = 0; i < length; i++) {
            int ascNum = (int) Math.floor(Math.random() * 26) + 97; // 97: a's ascii value
            randomStr += Character.toString((char) ascNum);
            //System.out.println("sendAutoComment: " + "(i = " + i + ") " +randomStr);
        }
        try {
            outputToServer.writeBytes(start + randomStr + "\n");
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

} 
