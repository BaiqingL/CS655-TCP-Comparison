// View Client lets user view a game played by a group of people
// Usage: java ViewClient <host> <port>
//   where host indicates View Server, which is Server's host name or IP address
//      host == localhost indicates Server is on the same computer as Client
//      port indicates the port (or port number) to which Server listens
// Example 1: java ViewClient localhost 58001
// Example 2: java ViewClient "192.168.0.100" 58001

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ViewClient {

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
      System.err.println("Usage: java ViewClient <host> <port>");
      System.exit(1);
    }

    try {
      System.out.println("Connecting to game view server on port " + port);
      Socket conSock = new Socket(hostname, port);
      TTTgame game = new TTTgame();
      DataOutputStream outputToServer = new DataOutputStream(conSock.getOutputStream());

      System.out.println("Connection established, viewer can comment.");

      // Start a thread to listen and display data received from view server
      ViewReceiver receiver = new ViewReceiver(conSock, game);
      Thread theThread = new Thread(receiver);
      theThread.start();

      // Read input/comment from the keyboard and send it to everyone else.
      Scanner keyboard = new Scanner(System.in);
      while (outputToServer != null) {
        String data = keyboard.nextLine();
        if (data.equals("quit")) {
          outputToServer.writeBytes("!\n");
          outputToServer.close();
          outputToServer = null;
          break;
        } else {
          outputToServer.writeBytes(data + "\n");
        }
      } // while
      System.out.println("Connection lost/closed.");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

} // View Client
