// View Server sends the game moves to game viewers live
// Example usage: java ViewServer 58001

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ViewServer {

  // Maintain list of all view client sockets for broadcast
  private final Socket[] socketList;

  public ViewServer() {
    socketList = new Socket[100];
    for (int i = 0; i < 100; i++) {
      socketList[i] = null;
    }
  }

  public static void main(String[] args) {
    int port = 0; // port to which Server listens

    // get host and port from command arguments
    if (args.length == 1) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args[0] + " must be an unsigned number.");
        System.exit(1);
      }
    } else {
      System.err.println("Usage: java Server <port>");
      System.exit(1);
    }

    ViewServer server = new ViewServer();
    server.getConnection(port);
  }

  private void getConnection(int port) {
    // Wait for a connection from the client
    try {
      System.out.println("Waiting for viewer connections on port " + port);
      ServerSocket serverSock = new ServerSocket(port);
      // This is an infinite loop, the user will have to shut it down
      // using control-c

      int playerID = 1;
      // assume the first 2 connections are from 2 players for publishing game moves
      int i = 0;
      for (i = 0; i < 2; i++) { // for each player
        Socket conSock = serverSock.accept();
        this.socketList[i] = conSock;
        System.out.println("Player " + (i + 1) + " connected successfully.");
        if (i == 0) { // one PubHandler provides enough information for game
          PubHandler handler = new PubHandler(conSock, this.socketList, playerID);
          Thread theThread = new Thread(handler);
          theThread.start();
        }
      }

      // The following connections are from viewers (subscribers) for viewing game live
      for (i = 2; i < 4; ++i) {
        Socket conViewSock = serverSock.accept();
        this.socketList[i] = conViewSock;
        System.out.println("Viewer " + (i - 1) + " connected successfully.");

        CommentHandler handler = new CommentHandler(conViewSock, this.socketList, playerID);
        Thread theThread = new Thread(handler);
        theThread.start();

        // SubHandler handler = new SubHandler(connectionSock, this.socketList, playerID);
        // Thread theThread = new Thread(handler);
        // theThread.start();
        // playerID -= 2;
      }

      System.out.println("View Server running...");
      Socket conSockt = serverSock.accept(); // change/add code for more viewers

      for (i = 0; i < this.socketList.length; ++i) {
          if (socketList[i] != null) {
              socketList[i].close();
          }
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

}
