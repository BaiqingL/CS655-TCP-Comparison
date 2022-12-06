
// Game Client
// Usage: java GameClient <host> <port> [<playerName> <timeLimit> [Auto <interval>]]
//   where host indicates Server, which is Server's host name or IP address
//      host = localhost indicates Server is on the same computer as Client
//      port indicates the port (or port number) to which Server listens
// Example 1: java GameClient "localhost" 58000
// Example 2: java GameClient "192.168.0.100" 58000
// Example 3: java GameClient "192.168.0.100" 58000 Alex 66
// Example 4: java GameClient "192.168.0.100" 58000 Alex 66 Auto 2000

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class GameClient {
    public static long[] comTimes = {0, 0};

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

    /**
     * prompt the user for an int between min and max inclusive
     *
     * @param promptMsg: message to print out to ask the user for an int
     * @param min:       inclusive lower bound, int
     * @param max:       inclusive upper bound, int
     * @return valid int given by user
     */
    public static int promptInt(String promptMsg, int min, int max) {
        System.out.println(promptMsg);
        Scanner sc = new Scanner(System.in);
        boolean isValidInput = false;
        String userInput;
        int val = 0;
        do {
            userInput = sc.nextLine();
            try {
                val = Integer.parseInt(userInput);
                if (val >= min && val <= max) {
                    isValidInput = true;
                }
                else {
                    System.out.println("Value must be between " + min + " and " + max + " inclusive. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("invalid input");
            }
        } while (!isValidInput);
        return val;
    }

    public static void main(String[] args) {
        String hostname = null; // Server host 
        int port = 0; // port to which Server listens
        String playerName = null;
        int timeLimit = 60;
        String autoStr = null;
        String interval = "2000";

        // get host and port from command arguments
        if (args.length >= 2) {
            hostname = args[0];
            try {
                port = Integer.parseInt(args[1]);
                if (args.length >= 4) {
                    playerName = args[2];
                    timeLimit = Integer.parseInt(args[3]);
                }
                if (args.length >= 6) {
                    autoStr = args[4];
                    interval = args[5];
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an unsigned number.");
                System.exit(1);
            }
        }
        else {
            System.err.println("Usage: java Client <host> <port> [<playerName> <timeLimit> [Auto <interval>]]");
            System.exit(1);
        }

        try {
            System.out.println("Connecting to game server on port " + port);
            Socket conSock = new Socket(hostname, port);
            TTTgame game = new TTTgame();
            DataOutputStream outputToServer = new DataOutputStream(conSock.getOutputStream());

            System.out.println("Connection established.");

            // Start a thread to listen and display data sent by the server
            GameListener listener = new GameListener(conSock, game, comTimes);
            Thread theThread = new Thread(listener);
            theThread.start();

            // Read input from the keyboard/client and send it to the game server
            Scanner keyboard = new Scanner(System.in);
            int moveInputs = 0;
            boolean playerNameSentToGameServer = false;
            if (playerName == null) {
                playerName = promptString("What is player's name?");
                timeLimit = promptInt("What is the time limit (in seconds) for each player?", 1, 3600);
            }

            while (outputToServer != null) {
                if (!playerNameSentToGameServer) {
                    outputToServer.writeBytes("player name: " + playerName + "\n");
                    outputToServer.writeBytes("time limit: " + timeLimit + "\n");

                    if (autoStr != null) {
                        outputToServer.writeBytes(autoStr + " " + interval + "\n");
                    }
                    playerNameSentToGameServer = true;
                }

                String data = keyboard.nextLine();
                if (data.equals("quit")) {
                    outputToServer.writeBytes(data + "\n");
                    Thread.sleep(200); // sleep 0.2 second
                    outputToServer.close();
                    conSock.close();
                    outputToServer = null;

                    break;
                }
                else if ((data.startsWith("0") || data.startsWith("1")) || data.startsWith("2")) {
                    comTimes[0] = System.currentTimeMillis();
                    outputToServer.writeBytes(data + "\n");
                }
                else if (data.startsWith("Auto ")) {
                    outputToServer.writeBytes(data + "\n");
                }
                else {
                    System.out.println("Invalid input, pleas try again.");
                }
            }

            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException z) {
            System.out.println(z.getMessage());
        }
    }
}
