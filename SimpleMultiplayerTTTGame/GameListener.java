// Game Listener
// receives data from game server: game board, turn, or move request

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class GameListener implements Runnable {
    private Socket connectionSock = null;
    public TTTgame game;

    GameListener(Socket sock, TTTgame game) {
        this.connectionSock = sock;
        this.game = game;
    }

    public void run() {
        // Wait for data from the server.  If received, output it.
        try {
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
            while (true) {
                if (serverInput == null) {
                    // Connection has issue
                    System.out.println("Closing connection for socket " + connectionSock);
                    connectionSock.close();
                    break;
                }
                // Get data sent from the server
                String serverText = serverInput.readLine();

                if (serverText.startsWith("#")) {
                    this.game.printBoard(serverText.substring(1));
                } else {
                    System.out.println(serverText);
                }
            } // while
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        } // try

    } // run()

} // Game Listener for Game Client
