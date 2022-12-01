// View Receiver receives game moves from View Server and displays them

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ViewReceiver implements Runnable {

  public TTTgame game;
  private Socket conSock = null;

  ViewReceiver(Socket sock, TTTgame game) {
    this.conSock = sock;
    this.game = game;
  }

  public void run() {
    // Wait for data from the server.  If received, output it.
    try {
      BufferedReader inputFromServer = new BufferedReader(
          new InputStreamReader(this.conSock.getInputStream()));
      while (true) {
        if (inputFromServer == null) {
          // Connection issue
          System.out.println("Closing connection for socket " + conSock);
          conSock.close();
          break;
        }
        // Get data sent from the server
        String serverText = inputFromServer.readLine();

        //System.out.println(serverText);

        if (serverText.startsWith("X#") || serverText.startsWith("O#")) {
          // get time stamp and compute network delay for broadcast
          String[] lines = serverText.substring(2).split(";");
          // System.out.println(serverText);
          // System.out.println("timeStamp: " + lines[0]);
          // System.out.println( serverText.substring(3+lines[0].length()) );
          long timeStamp = Long.parseLong(lines[0]); // timeStamp = time starting to broadcast
          long currentTime = System.currentTimeMillis();

          this.game.printBoard(serverText.substring(3 + lines[0].length()));
          System.out.println("Game broadcast delay: " + (currentTime - timeStamp));
        } else if (serverText.startsWith("!")) {
          conSock.close();
          break;
        } else {
          System.out.println(serverText);
        }
      } // while
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }

} 
