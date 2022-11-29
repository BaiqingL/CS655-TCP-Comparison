// PubHandler receives the game moves from a player (publisher) and
//   broadcasts to viewers (subscribers) for this player

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class PubHandler implements Runnable {

  public Socket connectionSock;
  public Socket[] socketList;
  public TTTgame game;
  public int playerID;
  public DataOutputStream[] clientOutputs; 

  public PubHandler(Socket sock,Socket[] socketList, int playerID) {
	this.connectionSock = sock;
	this.socketList = socketList;	// Keep reference to master list
    	this.playerID = playerID;
        this.clientOutputs = new DataOutputStream[10]; 
        for (int i = 0; i < 10; i++) {
            this.clientOutputs[i] = null;
        }
  }

  public void run() {
       // Wait for data from the player. after receiving it, broadcast it to viewers.
	try {
		BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
		while (true) {
			if (serverInput == null) {
				// Connection was lost
				System.out.println("Closing connection for socket " + connectionSock);
				connectionSock.close();
				break;
			}
			// Get data sent from the player
			String serverText = serverInput.readLine();
            // broardcast it to viewers
			broadcastMessage(serverText + "\n");
		}
	} catch (Exception e) {
		System.out.println("Error: " + e.toString());
	}
  }

  private void broadcastMessage(String message) { // send message to viewers in socketList
    try {
      int i = 2;
      while (this.socketList[i] != null) {
         if ( this.clientOutputs[i] == null) {
             this.clientOutputs[i] = new DataOutputStream(this.socketList[i].getOutputStream());
         }
         if (this.clientOutputs[i] != null) {
             this.clientOutputs[i].writeBytes(message);
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
