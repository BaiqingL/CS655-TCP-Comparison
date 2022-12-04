// CommentHandler receives a comment from a viewer of a game and
//   broadcasts the comment to the other viewers of the game

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class CommentHandler implements Runnable {

  public Socket conSock;
  public Socket[] socketList;
  public TTTgame game;
  public int playerID;
  public DataOutputStream[] serverOutputToViewers; 
  public int gameIndex;
  public int playerIndex;
  public int viewerIndex;
  public String viewerName;

  public void CommentHandler1(Socket sock,Socket[] socketList, int playerID) {
	this.conSock = sock;
	this.socketList = socketList;	// Keep reference to master list
    	this.playerID = playerID;
        this.serverOutputToViewers = new DataOutputStream[10]; 
        for (int i = 0; i < 10; i++) {
            this.serverOutputToViewers[i] = null;
        }
  }


  public CommentHandler(int[] indexes, ViewStage[] refStages) {
    this.gameIndex = indexes[0];
    this.playerIndex = indexes[1];
    this.viewerIndex = indexes[2];
    this.conSock = refStages[this.gameIndex].viewerSockets[this.viewerIndex];
    this.socketList = refStages[this.gameIndex].viewerSockets;	// Keep reference to master list
    this.game = refStages[this.gameIndex].game;
    this.playerID = refStages[this.gameIndex].playerIDs[this.playerIndex];
    this.viewerName = refStages[this.gameIndex].viewers[this.viewerIndex];

    int len = this.socketList.length;
    this.serverOutputToViewers = new DataOutputStream[len]; 
    for (int i = 0; i < len; i++) {
        this.serverOutputToViewers[i] = null;
    }
  }

  public void run() {
     // Wait for comment from a viewer. after receiving it, broadcast it to the other viewers.
      try {
        BufferedReader inputFromViewer = new BufferedReader(new InputStreamReader(conSock.getInputStream()));
        while (true) {
            if (inputFromViewer == null) {
                // Connection was lost
                System.out.println("Closing connection for socket " + conSock);
                conSock.close();
                break;
            }
            // Get comment from the viewer
            String textFromViewer = inputFromViewer.readLine();
            if (textFromViewer.equals("quit")) {
                for (int i = 0; i < this.socketList.length; i++) {
                    if (this.socketList[i] == conSock) {
                       this.serverOutputToViewers[i].writeBytes(textFromViewer + "\n");
                       this.socketList[i] = null;
                       break;
                    }
                }
                conSock.close();
                break;
            }

            if (textFromViewer.startsWith("Auto ")) { // Auto <length> <interval> 
                // broardcast it to all viewers
                broadcastMessage(this.viewerName + "'s Comment: " + textFromViewer + "\n", conSock, true);
            } else {
                // broardcast it to the other viewers
                broadcastMessage(this.viewerName + "'s Comment: " + textFromViewer + "\n", conSock, false);
            }
        } // while
      } catch (Exception e) {
        System.out.println("Error: " + e.toString());
      }
  }

  private void broadcastMessage(String message, Socket sock, boolean all) { // send message to viewers in socketList
    try {
      int i = 0;
      while (this.socketList[i] != null) {
         if ( this.serverOutputToViewers[i] == null) {
             this.serverOutputToViewers[i] = new DataOutputStream(this.socketList[i].getOutputStream());
         }
         if (this.serverOutputToViewers[i] != null) {
             if (all) {
                 this.serverOutputToViewers[i].writeBytes(message);
             } else if (this.socketList[i] != sock) {
                 this.serverOutputToViewers[i].writeBytes(message);
             } // else: not to otiginator 
	     //System.out.println("broadcasted (" + i + " conSocket): " + this.socketList[i] + "; " + message);
         }
	 //System.out.println("broadcast (" + i + "): " + message);
         i++;
      } // while
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
  }

}
