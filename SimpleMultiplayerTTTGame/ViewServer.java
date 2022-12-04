// View Server sends the game moves to game viewers live
// Example usage: java ViewServer 58001

import java.net.*;
import java.io.*;
import java.util.*;

public class ViewServer {
    // Maintain list of all view client sockets for broadcast
    public ViewStage[] viewStages;
    public static final int MAX_NUMBER_OF_GAMES = 50;
    public static final int MAX_NUMBER_OF_VIEWERS = 100;

    public ViewServer() {
        this.viewStages = new ViewStage[MAX_NUMBER_OF_GAMES];
        for (int i = 0; i < MAX_NUMBER_OF_GAMES; i++) {
            this.viewStages[i] = new ViewStage();
        }
    }

    // get the index of the game being plaued by playerName   
    public int getGameIndex(String playerName) {
        for (int i = 0; i < MAX_NUMBER_OF_GAMES; i++) {
            if (this.viewStages[i].players[0].equals(playerName) ||
                    this.viewStages[i].players[1].equals(playerName)) {
                return i;
            }
        }
        return -1;
    }

    // add viewer to the game being plaued by playerName
    // return: gameIndex, playerIndex, viewerIndex
    public int[] addViewerToGame(String playerName, String viewerName, Socket viewSock) {
        int[] indexes = {-1, -1, -1};
        for (int i = 0; i < MAX_NUMBER_OF_GAMES; i++) {
            if (this.viewStages[i].players[0].equals(playerName) ||
                    this.viewStages[i].players[1].equals(playerName)) {

                for (int j = 0; j < MAX_NUMBER_OF_VIEWERS; j++) {
                    if (this.viewStages[i].viewers[j] == null) {
                        this.viewStages[i].viewers[j] = viewerName;
                        this.viewStages[i].viewerSockets[j] = viewSock;
                        indexes[0] = i;
                        if (this.viewStages[i].players[0].equals(playerName)) {
                            indexes[1] = 0;
                        }
                        else {
                            indexes[1] = 1;
                        }
                        indexes[2] = j;
                        return indexes;
                    }
                }
            }
        }
        return indexes;
    }

    private void getConnection(int port) {
        // Wait for a connection from the client
        try {
            System.out.println("Waiting for viewer connections on port " + port);
            ServerSocket serverSock = new ServerSocket(port);

            int playerCount = 0;

            String playerInfoTag = "player info: ";
            String playerInfoReceived = null;
            String viewerInfoTag = "viewer info: ";
            String viewerInfoReceived = null;

            while (true) {

                Socket conSock = serverSock.accept();

                BufferedReader inputFromPlayerViewer =
                        new BufferedReader(new InputStreamReader(conSock.getInputStream()));

                String line = inputFromPlayerViewer.readLine(); // get player's or viewer's info

                if (line.startsWith(viewerInfoTag)) {
                    viewerInfoReceived = line.substring(viewerInfoTag.length());

                    // viewer info: viewerName, playerName
                    String[] lines = viewerInfoReceived.split(",");
                    String viewerName = lines[0];
                    String playerName = lines[1];

                    int[] indexes = addViewerToGame(playerName, viewerName, conSock);

                    if (indexes[0] != -1) {
                        System.out.println("Viewer: " + viewerName + " connected.");
                    }
                    else {
                        System.out.println("Viewer: " + viewerName + " not connected.");
                    }

                    CommentHandler handler = new CommentHandler(indexes, this.viewStages);
                    Thread theThread = new Thread(handler);
                    theThread.start();

                }
                else if (line.startsWith(playerInfoTag)) {
                    playerInfoReceived = line.substring(playerInfoTag.length());

                    // player info: gameIndex, playerIndex, playerName, playerID
                    String[] lines = playerInfoReceived.split(",");
                    int gameIndex = Integer.parseInt(lines[0]);
                    int playerIndex = Integer.parseInt(lines[1]);
                    String playerName = lines[2];
                    int playerID = Integer.parseInt(lines[3]);

                    this.viewStages[gameIndex].players[playerIndex] = playerName;
                    this.viewStages[gameIndex].playerIDs[playerIndex] = playerID;
                    this.viewStages[gameIndex].playerPublishSockets[playerIndex] = conSock;

                    playerCount++;
                    System.out.println("Player " + Integer.toString(playerCount) + " connected.");
                    if (playerCount % 2 == 1) { // one PubHandler provides enough information for game
                        PubHandler handler = new PubHandler(gameIndex, playerIndex, this.viewStages);
                        // PubHandler handler = new PubHandler(conSock, this.socketList, playerID);
                        Thread theThread = new Thread(handler);
                        theThread.start();
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 0; // port to which Server listens

        // get host and port from command arguments
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an unsigned number.");
                System.exit(1);
            }
        }
        else {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }

        ViewServer server = new ViewServer();
        server.getConnection(port);
    }

}
