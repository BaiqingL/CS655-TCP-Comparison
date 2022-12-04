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
            String myPlaySymbol = "X";
            String otherPlaySymbol = "O";
            int numberOfMyTurn = -1;
            boolean computeDelay = false;
            boolean playerDetermined = false;
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
                    }
                    else {
                        System.out.println("startsWith You are player 'O',\n");
                        myPlaySymbol = "O";
                        otherPlaySymbol = "X";
                    }
                }

                if (textFromServer.startsWith("Please enter a column")) {
                    computeDelay = true;
                }
                if (textFromServer.equals("quit")) {
                    break;
                }
                if (textFromServer.startsWith(myPlaySymbol + "#") ||
                        textFromServer.startsWith(otherPlaySymbol + "#")) {
                    this.game.printBoard(textFromServer.substring(2));
                    if (computeDelay) {
                        long curTime = System.currentTimeMillis();
                        long networkDelay = curTime - this.comTimes[0];
                        System.out.println("Network Delay : " + networkDelay + " (ms)\n");
                        computeDelay = false;
                    }
                }
                else {
                    System.out.println(textFromServer);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

    }

}
