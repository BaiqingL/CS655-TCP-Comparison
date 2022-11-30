// Tic Tac Toe Game Handler sends input prompts for move to player (client), receives the input from client,
// plays game by the move according to the input, and sends updated game board to client

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class GameHandler implements Runnable {

  public Socket conSock;
  public Socket[] socketList;
  public TTTgame game;
  public int playerID;
  public Socket viewSock;
  String viewHostname;
  int viewPort;

  public GameHandler(Socket sock,Socket[] socketList, TTTgame game, int playerID, String viewHost, int viewPort) {
    this.conSock = sock;
    this.socketList = socketList;	// Keep reference to master list
    this.game = game;
    this.playerID = playerID;
    this.viewHostname = viewHost;
    this.viewPort = viewPort; // to be general late
  }

  public void run() {
    try {
      BufferedReader inputFromPlayer = new BufferedReader(new InputStreamReader(this.conSock.getInputStream()));

      this.viewSock = new Socket(this.viewHostname, this.viewPort);
      String playerSymbol = "X";
      boolean timeCounted = true;
      boolean gameStarted = false;
      long startMoveTime = 0;
      long endMoveTime = 0;

      switch (this.playerID) {
        case -1:
          sendMessage("You are player 'O', you will go second." + "\r\n");
          playerSymbol = "O";
          break;
        case 1:
          sendMessage("You are player 'X', you will go first." + "\r\n");
          playerSymbol = "X";
          break;
        default:
          break;
      }

      while (this.game.checkWin() == 0) {
        sendMessage(playerSymbol + this.game.printState() + "\r\n");
        long timeStamp = System.currentTimeMillis(); 
        String timeStampStr = playerSymbol + "#" + Long.toString(timeStamp) + ";";
        sendMessageToView(timeStampStr + this.game.printState() + "\r\n"); // message with timeStamp to view

        if (this.game.playerMove == this.playerID) {
          // my turn
          if (timeCounted && gameStarted) { // Time for Invalid move is counted, time for first move is not counted
              startMoveTime = System.currentTimeMillis();  
              timeCounted = false;
          }
          sendMessage("Please enter a row (0-2): " + "\r\n");
          String row = inputFromPlayer.readLine().trim();
          sendMessage("Please enter a column (0-2): " + "\r\n");
          String col = inputFromPlayer.readLine().trim();
          if (!(this.game.submitMove(Integer.parseInt(row), Integer.parseInt(col)))) {
            sendMessage("Invalid move." + "\r\n");
          } else { // moved 
            if (!gameStarted) {
                gameStarted = true;
            } else {
               endMoveTime = System.currentTimeMillis(); 
               long timeUsed = endMoveTime - startMoveTime;
               long timeTotal = this.game.countTime(timeUsed, this.playerID);
               sendMessage("Time used in this move: " + timeUsed + "(ms), Total time used: " + timeTotal + "(ms)\n");
               timeCounted = true;
            }
          }
        } else {
          // other player's turn
          sendMessage("Please wait for opponent's move." + "\r\n");
          while (this.game.playerMove != this.playerID) {
            Thread.sleep(500);
          }
        }
      }

      sendMessage(playerSymbol + this.game.printState());
      long timeStamp = System.currentTimeMillis(); 
      String timeStampStr = playerSymbol + "#" + Long.toString(timeStamp) + ";";
      sendMessageToView(timeStampStr + this.game.printState() + "\r\n"); // message with timeStamp to view

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
          DataOutputStream outputToPlayer = new DataOutputStream(this.conSock.getOutputStream());
          outputToPlayer.writeBytes(message);
	    //System.out.println(message);
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }

  private void sendMessageToView(String message) { 
      try {
          DataOutputStream outputToViewServer = new DataOutputStream(this.viewSock.getOutputStream());
          outputToViewServer.writeBytes(message);
	    //System.out.println(message);
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }


}
