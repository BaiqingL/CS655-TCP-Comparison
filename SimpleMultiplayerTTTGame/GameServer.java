// usage: java GameServer <port> <viewServer> <viewPort>
// Example: java GameServer 58000 "localhost" 58001
// Example: java GameServer 58000 "10.10.172.190" 58001

import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer {
    // Maintain list of all client sockets for broadcast
    private Socket[] socketList;
    private String viewHostname;
    private int viewPort;

    public GameServer(String viewHost, int port) {
        this.socketList = new Socket[2];
        this.viewHostname = viewHost;
        this.viewPort = port;
    }

    private void getConnection(int port) {
        // Wait for a connection from the client
        try {
            System.out.println("Waiting for player connections on port " + port);
            ServerSocket serverSock = new ServerSocket(port);

            TTTgame game = new TTTgame();

            int playerID = 1;

            for (int i = 0; i < 2; ++i) {
                Socket conSock = serverSock.accept();
                // Add this socket to the list
                this.socketList[i] = conSock;
                // Send to ClientHandler the socket and arraylist of all sockets

                System.out.println("Player " + Integer.toString(i+1) + " connected.");

                GameHandler handler = new GameHandler(conSock, this.socketList, game, playerID, this.viewHostname, this.viewPort);
                Thread theThread = new Thread(handler);
                theThread.start();
                playerID -= 2;
            }

            System.out.println("Game running...");

            Socket conSock = serverSock.accept(); // change/add code for more game player pairs

            for (int i = 0; i < this.socketList.length; ++i){
               socketList[i].close();
            }

            // Will never get here, but if the above loop is given
            // an exit condition then we'll go ahead and close the socket
            //serverSock.close();
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
