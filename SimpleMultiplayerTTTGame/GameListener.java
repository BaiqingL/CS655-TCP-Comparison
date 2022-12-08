// Game Listener
// receives data from game server: game board, turn, or move request

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class GameListener implements Runnable {
    private Socket conSock = null;
    public TTTgame game;
    public long[] comTimes;

    GameListener(Socket sock, TTTgame game, long[] comTimes) {
        this.conSock = sock;
        this.game = game;
        this.comTimes = comTimes;
    }

    public void run() {
        // Wait for data from the server.  If received, output it.
        try {
            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(conSock.getInputStream()));
            DataOutputStream outputToServer = new DataOutputStream(conSock.getOutputStream());

            String myPlaySymbol = "X";
            String otherPlaySymbol = "O";
            int numberOfMyTurn = -1;
            boolean computeDelay = false;
            boolean playerDetermined = false;
            int row = 0;
            int col = 0;
            int interval = 500;
            boolean autoPlay = false;
            while (true) {
                if (inputFromServer == null) {
                    // Connection has issue
                    System.out.println("Closing connection for socket " + conSock);
                    conSock.close();
                    break;
                }
                // Get data sent from the server
                String textFromServer = inputFromServer.readLine();
                if (!playerDetermined) {
                    playerDetermined = true;
                    if (textFromServer.startsWith("You are player 'X',")) {
                        System.out.println("startsWith You are player 'X',\n");
                        myPlaySymbol = "X";
                        otherPlaySymbol = "O";
                    } else {
                        System.out.println("startsWith You are player 'O',\n");
                        myPlaySymbol = "O";
                        otherPlaySymbol = "X";
                    }
                }

                if (textFromServer.startsWith("Auto ")) {
                    autoPlay = true;
                    interval = Integer.parseInt(textFromServer.substring(5));
                }

                if (textFromServer.startsWith("Please enter")) {
                    computeDelay = true;
                    //this.comTimes[0] = System.currentTimeMillis();
                }
                if (textFromServer.equals("quit")) {
                    break;
                }
                if (textFromServer.startsWith(myPlaySymbol + "#") || 
                    textFromServer.startsWith(otherPlaySymbol + "#") ) {
                    this.game.printBoard(textFromServer.substring(2));
                    if (computeDelay && textFromServer.startsWith(myPlaySymbol + "#")) {
                        long curTime = System.currentTimeMillis();
                        long networkDelay = curTime - this.comTimes[0];
                        System.out.println("Network Delay : " + networkDelay + " (ms)\n");
                        computeDelay = false;
                        Thread.sleep(interval); 
                    }
                    int [][] intBoard = this.game.boardToIntArray(textFromServer.substring(2));
                    int [] move = randomMove(intBoard);
                    row = move[0];
                    col = move[1];
                } else {
                    if (!textFromServer.startsWith("Auto") ) {
                        System.out.println(textFromServer);
                    }
                    if (autoPlay && textFromServer.startsWith("Please enter") ) {
                       this.comTimes[0] = System.currentTimeMillis();
                       outputToServer.writeBytes(Integer.toString(row) + "," + Integer.toString(col) + "\n");

//                       Thread.sleep(interval); 
                    }
                }
            } // while
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }  // try

    } // run()

    public int[] randomMove(int[][] intBoard) {
        int numberOfPossibleMoves = 0;
        int [] move = new int[2];
        for (int k = 0; k < 3; k++) {
          for (int j = 0; j < 3; j++) {
            if (intBoard[k][j] == 0) {
                numberOfPossibleMoves++;
            }
          }
        }
        int ranNumber = (int)Math.floor(Math.random()*numberOfPossibleMoves + 1);
        int availableMoveCount = 0;
        for (int k = 0; k < 3; k++) {
          for (int j = 0; j < 3; j++) {
            if (intBoard[k][j] == 0) {
                availableMoveCount++;
                if (availableMoveCount == ranNumber) {
                    move[0] = k;
                    move[1] = j;
                    return move;
                }
            }
          }
        }
        move[0] = 0;
        move[1] = 0;
        return move; 
    }

} // Game Listener for Game Client
