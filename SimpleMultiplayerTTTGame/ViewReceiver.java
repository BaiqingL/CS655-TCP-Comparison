// View Receiver receives game moves from View Server and displays them

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class ViewReceiver implements Runnable {
    private Socket conSock = null;
    public TTTgame game;
    public static final long DELAY_ACCEPTABLE = 200; // 0.2 second
    public static final String DELAY_UNACCEPTABLE_WARNING = "????? BROADCAST QUALITY NOT ACCEPTABLE ?????";

    ViewReceiver(Socket sock, TTTgame game) {
        this.conSock = sock;
        this.game = game;
    }

    public void run() {
        // Wait for data from the server. If received, output it.
        try {
            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(this.conSock.getInputStream()));
            DataOutputStream outputToServer = new DataOutputStream(this.conSock.getOutputStream());

            boolean commentGeneratorStarted = false;
            int[] parameters = {0, 0}; // length, interval

            while (true) {
                if (inputFromServer == null) {
                    // Connection issue
                    System.out.println("Closing connection for socket " + conSock);
                    conSock.close();
                    break;
                }
                // Get data sent from the server
                String textFromServer = inputFromServer.readLine();

                // System.out.println("ViewReceiver: " + textFromServer);

                if (textFromServer.startsWith("X#") || textFromServer.startsWith("O#")) {
                    // get time stamp and compute network delay for broadcast
                    String[] lines = textFromServer.substring(2).split(";");
                    // System.out.println(textFromServer);
                    // System.out.println("timeStamp: " + lines[0]);
                    // System.out.println( textFromServer.substring(3+lines[0].length()) );
                    long timeStamp = Long.parseLong(lines[0]); // timeStamp = time starting to broadcast
                    long currentTime = System.currentTimeMillis();
                    //System.out.println("Before printBoard: " + textFromServer.substring(4+lines[0].length()) );
                    this.game.printBoard(textFromServer.substring(4 + lines[0].length()));
                    long broadcastDelay = currentTime - timeStamp;
                    System.out.println("Game broadcast delay: " + broadcastDelay);
                    if (broadcastDelay > DELAY_ACCEPTABLE) { // delay not acceptable
                        System.out.println(DELAY_UNACCEPTABLE_WARNING);
                    }
                }
                else if (textFromServer.startsWith("quit")) {
                    inputFromServer.close();
                    outputToServer.close();
                    conSock.close();
                    break;
                }
                else { // check for automatically send comments for experiments
                    // xx's comment: Auto <length> <interval> (length of comment sent,  a comment per interval (ms))
                    String[] lines = textFromServer.split(":");
                    if (lines.length > 1 && lines[1].startsWith(" Auto ")) {
                        //System.out.println("lines[0,1]:" + lines[0] + ", " + lines[1]);
                        // automatically send comments for experiments
                        String[] line2s = lines[1].substring(1).split(" "); // Auto length interval
                        //System.out.println("line2s[0,1,2]:" + line2s[0] + ", " + line2s[1] + ", " + line2s[2]);
                        if (line2s.length > 2) {
                            int autoCommentLength = Integer.parseInt(line2s[1]);
                            int autoCommentInterval = Integer.parseInt(line2s[2]);
                            int autoCommentMyID = (int) Math.floor(Math.random() * 1000 + 1);
                            String autoCommentStart = "AutoComment" + String.valueOf(autoCommentMyID);
                            parameters[0] = autoCommentLength;
                            parameters[1] = autoCommentInterval;

                            if (!commentGeneratorStarted) {
                                // Start a thread to generate comments
                                int ran30 = (int) Math.floor(Math.random() * 30 + 1);
                                Thread.sleep(ran30); // sleep 1 to 30 ms in random

                                CommentGenerator generator = new CommentGenerator(outputToServer, parameters, autoCommentStart);
                                Thread theThread = new Thread(generator);
                                theThread.start();
                                commentGeneratorStarted = true;
                            }
                        }
                    }
                    else { // not (lines.length > 1 && .. )
                        System.out.println(textFromServer);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

} 
