// Tic Tac Toe Game Handler
// sends input prompts for move to player (client), receives the input from client,
// plays game by the move according to the input, and sends updated game board to client

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class GameHandler implements Runnable {

  public Socket connectionSock;
  public Socket[] socketList;
  public TTTgame game;
  public int playerID;
  public Socket viewSock;
  String viewHostname;
  int viewPort;

  public GameHandler(Socket sock,Socket[] socketList, TTTgame game, int playerID, String viewHost, int viewPort) {
    this.connectionSock = sock;
    this.socketList = socketList;	// Keep reference to master list
    this.game = game;
    this.playerID = playerID;
    this.viewHostname = viewHost;
    this.viewPort = viewPort; // to be general late
  }

  public void run() {
    try {
      BufferedReader playerInput = new BufferedReader(new InputStreamReader(this.connectionSock.getInputStream()));

      this.viewSock = new Socket(this.viewHostname, this.viewPort);

      switch (this.playerID) {
        case -1:
          sendMessage("\nYou are player 'O', you will go second." + "\r\n");
          break;
        case 1:
          sendMessage("\nYou are player 'X', you will go first." + "\r\n");
          break;
        default:
          break;
      }

      while (this.game.checkWin() == 0) {
        sendMessage(this.game.printState() + "\r\n");
        sendMessageToView(this.game.printState() + "\r\n"); // message to view

        if (this.game.playerMove == this.playerID) {
          // my turn
          sendMessage("Please enter a row (0-2): " + "\r\n");
          String row = playerInput.readLine().trim();
          sendMessage("Please enter a column (0-2): " + "\r\n");
          String col = playerInput.readLine().trim();
          if (!(this.game.submitMove(Integer.parseInt(row), Integer.parseInt(col)))) {
            sendMessage("Invalid move." + "\r\n");
          } 
        } else {
          // other player's turn
          sendMessage("Please wait for opponent's move." + "\r\n");
          while (this.game.playerMove != this.playerID) {
            Thread.sleep(500);
          }
        }
      }

      sendMessage(this.game.printState());
      sendMessageToView(this.game.printState() + "\r\n"); // message to view

      int checkResult = this.game.checkWin();
      sendMessage(Integer.toString(checkResult) + "\r\n");
      if (checkResult == this.playerID) {
        sendMessage("GAME OVER! YOU WIN!" + "\r\n");
      } else if (checkResult == 2) {
        sendMessage("GAME OVER! TIE GAME!" + "\r\n");
      } else {
        sendMessage("GAME OVER! YOU LOSE!" + "\r\n");
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (InterruptedException z) {
      System.out.println(z.getMessage());
    }
  }

  private void sendMessage(String message) { 
      try {
          DataOutputStream clientOutput = new DataOutputStream(this.connectionSock.getOutputStream());
          clientOutput.writeBytes(message);
	    //System.out.println(message);
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }

  private void sendMessageToView(String message) { 
      try {
          DataOutputStream clientOutput = new DataOutputStream(this.viewSock.getOutputStream());
          clientOutput.writeBytes(message);
	    //System.out.println(message);
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }


}
