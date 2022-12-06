// PubHandler receives the game moves from a player (publisher) and
//   broadcasts to viewers (subscribers) for this player

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class PubHandler implements Runnable {

    public Socket conSock;
    public Socket[] socketList;
    public TTTgame game;
    public int playerID;
    public DataOutputStream[] outputToViewers;
    public int gameIndex;
    public int playerIndex;


    public PubHandler(int gameIndex, int playerIndex, ViewStage[] refStages) {
        this.conSock = refStages[gameIndex].playerPublishSockets[playerIndex];
        this.socketList = refStages[gameIndex].viewerSockets;    // Keep reference to master list
        this.game = refStages[gameIndex].game;
        this.playerID = refStages[gameIndex].playerIDs[playerIndex];
        this.gameIndex = gameIndex;
        this.playerIndex = playerIndex;

        int len = this.socketList.length;
        this.outputToViewers = new DataOutputStream[len];
        for (int i = 0; i < len; i++) {
            this.outputToViewers[i] = null;
        }
    }

    public void run() {
        // Wait for data from the player. after receiving it, broadcast it to viewers.
        try {
            BufferedReader inputFromPublisher = new BufferedReader(new InputStreamReader(conSock.getInputStream()));
            while (true) {
                if (inputFromPublisher == null) {
                    // Connection was lost
                    System.out.println("Closing connection for socket " + conSock);
                    conSock.close();
                    break;
                }
                // Get data sent from the player
                String textFromPublisher = inputFromPublisher.readLine();
                // System.out.println("PubHandler received: " + textFromPublisher);
                // broardcast it to viewers
                broadcastMessage(textFromPublisher + "\n");

                if (textFromPublisher.equals("quit")) {
//                inputFromPublisher.close();
//                conSock.close();
                    break;
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    private void broadcastMessage(String message) { // send message to viewers in socketList
        try {
            int i = 0;
            while (this.socketList[i] != null) {
                if (this.outputToViewers[i] == null) {
                    this.outputToViewers[i] = new DataOutputStream(this.socketList[i].getOutputStream());
                }
                if (this.outputToViewers[i] != null) {
                    this.outputToViewers[i].writeBytes(message);
                    //System.out.println("broadcasted (" + i + " conSocket): " + this.socketList[i] + "; " + message);
                }
                //System.out.println("broadcast (" + i + "): " + message);
                i++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
