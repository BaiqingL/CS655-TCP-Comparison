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

  public CommentHandler(Socket sock,Socket[] socketList, int playerID) {
	this.conSock = sock;
	this.socketList = socketList;	// Keep reference to master list
    	this.playerID = playerID;
        this.serverOutputToViewers = new DataOutputStream[10]; 
        for (int i = 0; i < 10; i++) {
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
			String serverText = inputFromViewer.readLine();
                        // broadcast it to the other viewers
			broadcastMessage("Comment:" + serverText + "\n", conSock);
		}
	} catch (Exception e) {
		System.out.println("Error: " + e);
	}
  }

  private void broadcastMessage(String message, Socket sock) { // send message to viewers in socketList except sock
    try {
      int i = 2;
      while (this.socketList[i] != null) {
         if ( this.serverOutputToViewers[i] == null) {
             this.serverOutputToViewers[i] = new DataOutputStream(this.socketList[i].getOutputStream());
         }
         if (this.serverOutputToViewers[i] != null && this.socketList[i] != sock) {
             this.serverOutputToViewers[i].writeBytes(message);
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
