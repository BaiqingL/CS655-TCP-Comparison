// Game Client
// Usage: java GameClient <host> <port>
//   where host indicates Server, which is Server's host name or IP address
//      host == localhost indicates Server is on the same computer as Client
//      port indicates the port (or port number) to which Server listens
// Example 1: java GameClient localhost 58000
// Example 2: java GameClient "192.168.0.100" 58000

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {

  public static long[] comTimes = {0, 0};

  public static void main(String[] args) {
    String hostname = null; // Server host
    int port = 0; // port to which Server listens

    // get host and port from command arguments
    if (args.length == 2) {
      hostname = args[0];
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args[1] + " must be an unsigned number.");
        System.exit(1);
      }
    } else {
      System.err.println("Usage: java Client <host> <port>");
      System.exit(1);
    }

    try {
      System.out.println("Connecting to game server on port " + port);
      Socket conSock = new Socket(hostname, port);
      TTTgame game = new TTTgame();
      DataOutputStream outputToServer = new DataOutputStream(conSock.getOutputStream());

      System.out.println("Connection established.");

      // Start a thread to listen and display data sent by the server
      GameListener listener = new GameListener(conSock, game, comTimes);
      Thread theThread = new Thread(listener);
      theThread.start();

      // Read input from the keyboard/client and send it to the game server.
      Scanner keyboard = new Scanner(System.in);
      int moveInputs = 0;
      while (outputToServer != null) {
        String data = keyboard.nextLine();
        if (data.equals("quit")) {
          outputToServer.writeBytes(data + "\n");
          //Thread.sleep(100); // sleep 0.1 second
          outputToServer.close();
          outputToServer = null;
          break;
        } else if ((data.equals("0") || data.equals("1")) || data.equals("2")) {
          outputToServer.writeBytes(data + "\n");
          moveInputs++;
          if (moveInputs % 2 == 0) {
            comTimes[0] = System.currentTimeMillis();
          }
        } else {
          System.out.println("Invalid input, please try again.");
        }
      }

      System.out.println("Connection lost.");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
