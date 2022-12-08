// Tic Tac Toe Game Handler
// sends input prompts for move to player (client), receives the input from client,
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
  public String playerName;
  public String otherPlayerName;
  public Socket viewSock;
  public String viewHostname;
  public int viewPort;
  public int gameIndex;
  public int playerIndex;
  public GameStage[] gameStages;

  public DataOutputStream outputToViewServer;
  public DataOutputStream outputToPlayer;
  public BufferedReader inputFromPlayer;


  public GameHandler(int gameIndex, int playerIndex, GameStage[] refStages) {
    this.conSock = refStages[gameIndex].playerSockets[playerIndex];
    this.socketList = refStages[gameIndex].playerSockets;	// Keep reference to master list
    this.game = refStages[gameIndex].game;
    this.playerID = refStages[gameIndex].playerIDs[playerIndex];
    this.viewHostname = refStages[gameIndex].viewHostname;
    this.viewPort = refStages[gameIndex].viewPort; // to be general late
    this.gameIndex = gameIndex;
    this.playerIndex = playerIndex;
    this.playerName = refStages[gameIndex].players[playerIndex];
    this.gameStages  = refStages;

  }

  public void run() {
    try {
      this.inputFromPlayer = new BufferedReader(new InputStreamReader(this.conSock.getInputStream()));
      //this.outputToPlayer = new DataOutputStream(this.conSock.getOutputStream());
      //this.outputToViewServer = new DataOutputStream(this.viewSock.getOutputStream());

      this.viewSock = new Socket(this.viewHostname, this.viewPort);
      String playerSymbol = "X";
      boolean timeCounted = true;
      boolean gameStarted = false;
      boolean playerQuit = false;
      long startMoveTime = 0;
      long endMoveTime = 0;
      String playerNameTag = "player name: ";
      String timeLimitTag = "time limit: ";
      String quitTag = "quit";
      long timeLimit = 60000;
      String playerNameReceived = null;

      String line = inputFromPlayer.readLine(); // get player's name
      if (line.startsWith(playerNameTag) ) {
         playerNameReceived = line.substring(playerNameTag.length());
         this.playerName = playerNameReceived;
         gameStages[this.gameIndex].players[this.playerIndex] = playerNameReceived;

         // send player info such as name to View Server
         sendMessageToView("player info: " + this.gameIndex + "," + 
             this.playerIndex + "," + this.playerName + "," + this.playerID + ",\n"); 
      }

      line = inputFromPlayer.readLine(); // get time limit for each player
      if (line.startsWith(timeLimitTag) ) {
         String timeLimitStr =  line.substring(timeLimitTag.length());
         timeLimit = Long.parseLong(timeLimitStr) * 1000;
         this.game.setTimeLimit(timeLimit);
      }

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
      while (true) { // play again and again

       while (this.game.checkWin() == 0) {
        String gameState = this.game.printState();
        sendMessage(playerSymbol + gameState + "\r\n");
        long timeStamp = System.currentTimeMillis(); 
        String timeStampStr = playerSymbol + "#" + Long.toString(timeStamp) + ";";
        sendMessageToView(timeStampStr + gameState + "\r\n"); // message with timeStamp to view

        if (this.game.playerMove == this.playerID) {
          // my turn
          if (timeCounted && gameStarted) { // Time for Invalid move is counted, time for first move is not counted
              startMoveTime = System.currentTimeMillis();  
              timeCounted = false;
          }
          sendMessage("Please enter row (0-2), col (0-2): " + "\r\n");
          line = inputFromPlayer.readLine();
          if (quitMessage(line, quitTag)) {
            playerQuit = true;
            break;
          }
          if (line.startsWith("Auto ")) {
            sendMessage(line + "\n");
            sendMessage("Please enter row (0-2), col (0-2): " + "\n");
            line = inputFromPlayer.readLine();
          }
          String [] move;
          String row = "0";
          String col = "0";

          do { 
             move = line.split(",");
             if (move.length < 2) {
                sendMessage("Please enter row (0-2), col (0-2): " + "\n");
                line = inputFromPlayer.readLine();
             } else {
                row = move[0];
                col = move[1];
             }
          } while (move.length < 2);

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
      } // while

      if (playerQuit) {
        break;
      }
      String gameState = this.game.printState();
      sendMessage(playerSymbol + gameState + "\n");
      long timeStamp = System.currentTimeMillis(); 
      String timeStampStr = playerSymbol + "#" + Long.toString(timeStamp) + ";";
      sendMessageToView(timeStampStr + gameState + "\n"); // message with timeStamp to viewc

      int checkResult = this.game.checkWin();
      //sendMessage(Integer.toString(checkResult) + "\r\n");
      if (checkResult == this.playerID) {
        sendMessage("GAME OVER! YOU WIN!" + "\r\n"); 
        sendMessageToView("GAME OVER! " + this.playerName + " WIN!" + "\n");
      } else if (checkResult == 2) {
        sendMessage("GAME OVER! TIE GAME!" + "\r\n");
        sendMessageToView("GAME OVER! TIE GAME!" + "\n");
      } else {
        sendMessage("GAME OVER! YOU LOSE!" + "\r\n");
        this.otherPlayerName = gameStages[this.gameIndex].players[(this.playerIndex+1)%2];
        if (this.otherPlayerName != null) {
            sendMessageToView("GAME OVER! " + this.otherPlayerName + " WIN!" + "\n");
        } else {
            sendMessageToView("GAME OVER! " + this.playerName + "'s opponent WIN!" + "\n");
        }
      }
      Thread.sleep(500);
      if (this.playerID == 1) { // game is reset by one player
         this.game.resetGame();
         this.game.setTimeLimit(timeLimit);
      }
      sendMessage("Let us play again." + "\n");

     } // while (true)
     //this.conSock.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (InterruptedException z) {
      System.out.println(z.getMessage());
    }
  }

  private boolean quitMessage(String message, String quitTag) {
    try { 
       if (message.equals(quitTag)) {
          sendMessage(message + "\n");
          sendMessageToView(message + "\n");
          Thread.sleep(200); // sleep 0.2 second
          this.inputFromPlayer.close();
          this.outputToPlayer.close();
          //this.outputToViewServer.close();
          //this.conSock.close();
          return true;
       }
       return false;
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (InterruptedException z) {
      System.out.println(z.getMessage());
    }
    return false;
  }

  private void sendMessage(String message) { 
      try {
          if (this.outputToPlayer == null) {
              this.outputToPlayer = new DataOutputStream(this.conSock.getOutputStream());
          }
          if (this.outputToPlayer != null) {
              this.outputToPlayer.writeBytes(message);
	      //System.out.println(message);
          }
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }

  private void sendMessageToView(String message) { 
      try {
          if (this.outputToViewServer == null) {
             outputToViewServer = new DataOutputStream(this.viewSock.getOutputStream());
          }
          if (this.outputToViewServer != null) {
              this.outputToViewServer.writeBytes(message);
	      //System.out.println("GameHandler: " + message);
          }
      } catch (IOException e) {
          System.out.println(e.getMessage());
      }
  }


}
