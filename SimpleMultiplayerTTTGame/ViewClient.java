
// View Client lets user view a game played by a group of people
// Usage: java ViewClient <host> <port> [<viewrName> <playerName> [Auto <length> <interval>]]
//   where host indicates View Server, which is Server's host name or IP address
//      host == localhost indicates Server is on the same computer as Client
//      port indicates the port (or port number) to which Server listens
// Example 1: java ViewClient "localhost" 58001
// Example 2: java ViewClient "192.168.0.100" 58001 
// Example 3: java ViewClient "192.168.0.100" 58001 Paul Alex
// Example 4: java ViewClient "192.168.0.100" 58001 Paul Alex Auto 30 500

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class ViewClient {

    /**
     * prompt the user for a string, cannot be the empty string
     *
     * @param promptMsg: message to print out to ask the user for a string
     * @return valid string given by user
     */
    public static String promptString(String promptMsg) {
        System.out.println(promptMsg);
        Scanner sc = new Scanner(System.in);
        do {
            String userInput = sc.nextLine();
            if (!userInput.isEmpty()) {
                return userInput;
            }
            else {
                System.out.println("invalid input");
            }
        } while (true);
    }

    public static void main(String[] args) {
        String hostname = null; // Server host 
        int port = 0; // port to which Server listens
        String viewerName = null;
        String playerName = null;
        String autoStr = null;
        String length = "30";
        String interval = "500";

        // get host and port from command arguments
        if (args.length >= 2) {
            hostname = args[0];
            try {
                port = Integer.parseInt(args[1]);
                if (args.length >= 4) {
                    viewerName = args[2];
                    playerName = args[3];
                }
                if (args.length >= 7) {
                    autoStr = args[4];
                    length = args[5];
                    interval = args[6];
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an unsigned number.");
                System.exit(1);
            }
        }
        else {
            System.err.println("Usage: java ViewClient <host> <port> [<viewerName> <playerName> [Auto <length> <interval>]]");
            System.exit(1);
        }

        try {
            System.out.println("Connecting to game view server on port " + port);
            Socket conSock = new Socket(hostname, port);
            TTTgame game = new TTTgame();
            DataOutputStream outputToServer = new DataOutputStream(conSock.getOutputStream());

            System.out.println("Connection established, viewer can comment.");

            // Start a thread to listen and display data received from view server
            ViewReceiver receiver = new ViewReceiver(conSock, game);
            Thread theThread = new Thread(receiver);
            theThread.start();

            // Read input/comment from the keyboard and send it to everyone else.
            Scanner keyboard = new Scanner(System.in);
            boolean viewerInfoSentToViewServer = false;
            if (viewerName == null) {
                viewerName = promptString("What is viewer's name?");
                playerName = promptString("What is player's name for game?");
            }

            while (outputToServer != null) {
                if (!viewerInfoSentToViewServer) {
                    outputToServer.writeBytes("viewer info: " +
                            viewerName + "," + playerName + ",\n");

                    if (autoStr != null) {
                        outputToServer.writeBytes(autoStr + " " + length + " " + interval + " \n");
                    }
                    viewerInfoSentToViewServer = true;
                }

                String data = keyboard.nextLine();
                if (data.equals("quit")) {
                    outputToServer.writeBytes(data + "\n");
                    Thread.sleep(100); // sleep 0.1 second
                    outputToServer.close();
                    conSock.close();
                    outputToServer = null;
                    break;
                }
                else {
                    outputToServer.writeBytes(data + "\n");
                }
            }
            System.out.println("Connection lost/closed.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException z) {
            System.out.println(z.getMessage());
        }
    }

}
