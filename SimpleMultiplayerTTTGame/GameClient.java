
// Game Client
// Usage: java GameClient <host> <port>
//   where host indicates Server, which is Server's host name or IP address
//      host = localhost indicates Server is on the same computer as Client
//      port indicates the port (or port number) to which Server listens
// Example 1: java GameClient localhost 58000
// Example 2: java GameClient "192.168.0.100" 58000

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

public class GameClient {

    public static void main(String[] args) {
        String hostname = null; // Server host 
        int port = 0; // port to which Server listens

        // get host and port from command arguments
        if (args.length == 2) {
            hostname = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an unsigned number.");
                System.exit(1);
            }
        } else {
            System.err.println("Usage: java Client <host> <port>");
            System.exit(1);
        }

        try {
            System.out.println("Connecting to game server on port " + port);
            Socket conSock = new Socket(hostname, port);
            TTTgame game = new TTTgame();
            DataOutputStream serverOutput = new DataOutputStream(conSock.getOutputStream());

            System.out.println("Connection established.");

            // Start a thread to listen and display data sent by the server
            GameListener listener = new GameListener(conSock, game);
            Thread theThread = new Thread(listener);
            theThread.start();

            // Read input from the keyboard and send it to everyone else.
            // The only way to quit is to hit control-c, but a quit command
            // could easily be added.
            Scanner keyboard = new Scanner(System.in);
            while (serverOutput != null) {
                String data = keyboard.nextLine();
                if ((data.equals("0") || data.equals("1")) || data.equals("2")) {
                    serverOutput.writeBytes(data + "\n");
                } else if (data.equals("quit")) {
                    serverOutput.close();
                    serverOutput = null;
                } else {
                    System.out.println("Invalid input, pleas try again.");
                }
            }

            System.out.println("Connection lost.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
