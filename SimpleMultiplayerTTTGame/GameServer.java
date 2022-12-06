// usage: java GameServer <port> <viewServer> <viewPort>
// Example: java GameServer 58000 "localhost" 58001
// Example: java GameServer 58000 "10.10.172.190" 58001

import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer {
    // Maintain list of all client sockets for broadcast
    public GameStage[] gameStages;
    public String viewHostname;
    public int viewPort;
    public static final int MAX_NUMBER_OF_GAMES = 50;
    public static final int NUMBER_OF_PLAYERS = 2;

    public GameServer(String viewHost, int port) {
        this.gameStages = new GameStage[MAX_NUMBER_OF_GAMES];
        this.viewHostname = viewHost;
        this.viewPort = port;

        for (int i = 0; i < MAX_NUMBER_OF_GAMES; i++) {
            this.gameStages[i] = new GameStage(viewHost, port);
        }
    }

    private void getConnection(int port) {
        // Wait for a connection from the client
        try {
            System.out.println("Waiting for player connections on port " + port);
            ServerSocket serverSock = new ServerSocket(port);

            for (int i = 0; i < MAX_NUMBER_OF_GAMES; i++) {
                TTTgame game = new TTTgame();

                int playerID = 1;

                for (int j = 0; j < NUMBER_OF_PLAYERS; j++) {
                    Socket conSock = serverSock.accept();

                    this.gameStages[i].playerSockets[j] = conSock; // Add this socket to the list
                    this.gameStages[i].playerIDs[j] = playerID;

                    System.out.println("Player " + Integer.toString(j+1) + " connected.");

                    GameHandler handler = new GameHandler(i, j, gameStages); // game i and player j
                    Thread theThread = new Thread(handler);
                    theThread.start();
                    playerID -= 2;

                }

                System.out.println("Game " + Integer.toString(i+1) + " running...");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 0; // port to which Server listens
        String viewHostname = null;
        int viewPort = 0;

        // get host and ports from command arguments
        if (args.length == 3) {
            try {
                port = Integer.parseInt(args[0]);
                viewHostname = args[1];
                viewPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an unsigned number.");
                System.exit(1);
            }
        } else {
            System.err.println("Usage: java GameServer <port> <viewServer> <viewPort>");
            System.exit(1);
        }

        GameServer server = new GameServer(viewHostname, viewPort);
        server.getConnection(port);
    }
}
