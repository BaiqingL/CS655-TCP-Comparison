// simple tic tac toe

import java.io.*;
import java.util.*;
import java.lang.*;

public class TTTgame {
  public volatile int playerMove; //X = 1, O = -1
  private int[][] board; //3x3
  public long[] timeUsed;
  public long[] timeUsedLastMove;
  public long timeLimit;
  public static final long DEFAULT_TIME_LIMIT = 60000; // (60s = 60000ms)

  public TTTgame() {
    this.board = new int[3][3];
    this.playerMove = 1;
    this.timeUsed = new long[2];
    this.timeUsedLastMove = new long[2];
    this.timeUsed[0] = 0; // for player using 'X'
    this.timeUsed[1] = 0; // for player using 'O'
    this.timeLimit = DEFAULT_TIME_LIMIT;
  }

  public void resetGame() {
    this.board = new int[3][3];
    this.playerMove = 1;
    this.timeUsed = new long[2];
    this.timeUsedLastMove = new long[2];
    this.timeUsed[0] = 0;
    this.timeUsed[1] = 0;
    this.timeUsedLastMove[0] = 0;
    this.timeUsedLastMove[1] = 0;
    this.timeLimit = DEFAULT_TIME_LIMIT;
  }

  public boolean submitMove(int i, int j) {
    if (this.board[i][j] != 0) {
      return false;
    } else {
      this.board[i][j] = this.playerMove;
      this.playerMove = -this.playerMove;
      return true;
    }
  } 

  public long getTimeLimit() {
    return this.timeLimit;
  }

  public void setTimeLimit(long time) {
    this.timeLimit = time;
  }

  public long countTime(long time, int playerID) {
    if (playerID == 1) { // time used by player using 'X'
      this.timeUsed[0] += time;
      this.timeUsedLastMove[0] = time;
      return this.timeUsed[0];
    } else { // time used by player using 'O'
      this.timeUsed[1] += time;
      this.timeUsedLastMove[1] = time;
      return this.timeUsed[1];
    }
  }

  public long getTimeUsed() { // time used for playerMove
    if (this.playerMove == 1) { // time used by player using 'X'
      return this.timeUsed[0];
    } else { // time used by player using 'O'
      return this.timeUsed[1];
    }
  }

  public long getTimeUsedLastMove() { // time used for last move by playerMove
    if (this.playerMove == 1) { // time used by player using 'X'
      return this.timeUsedLastMove[0];
    } else { // time used by player using 'O'
      return this.timeUsedLastMove[1];
    }
  }

  public String printState() {
    String output = "#";
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 2; j++) {
         output += Integer.toString(this.board[i][j]) + ",";
      }
      output += Integer.toString(this.board[i][2]) + ";";
    }
    // Time used by 'X': Total, last move, left:
    output += "Time used by 'X': Total = " + Long.toString(timeUsed[0]) + 
              ", Last Move = " +  Long.toString(timeUsedLastMove[0]) + 
              ", Left = " + Long.toString(timeLimit - timeUsed[0]) + ";"; 
    // Time used by 'O': Total, last move, left
    output += "Time used by 'O': Total = " + Long.toString(timeUsed[1]) + 
              ", Last Move = " +  Long.toString(timeUsedLastMove[1]) + 
              ", Left = " + Long.toString(timeLimit - timeUsed[1]) + ";"; 
    return output;
  }

  public void printBoard(String boardData) {
    String[] lines = boardData.split(";");
    String[][] board = new String[3][3];
    for (int i = 0; i < 3; i++) {
      board[i] = lines[i].split(",");
    }
    for (int k = 0; k < 3; k++) {
      for (int j = 0; j < 3; j++) {
        if (board[k][j].equals("1")) {
          board[k][j] = "X";
        } else if (board[k][j].equals("-1")) {
          board[k][j] = "O";
        } else {
          board[k][j] = " ";
        }
      }
    }
    System.out.println("   0   1   2");
    System.out.format("0 %2s |%2s |%2s \n", 
                      board[0][0], board[0][1], board[0][2]);
    System.out.println("  ---|---|---");
    System.out.format("1 %2s |%2s |%2s \n", 
                      board[1][0], board[1][1], board[1][2]);
    System.out.println("  ---|---|---");
    System.out.format("2 %2s |%2s |%2s \n", 
                      board[2][0], board[2][1], board[2][2]);
    System.out.println(lines[3]); // time used by player using 'X'
    System.out.println(lines[4]); // time used by player using 'O'
  }

  public int[][] boardToIntArray(String boardData) {
    String[] lines = boardData.split(";");
    String[][] board = new String[3][3];
    int[][] intBoard = new int[3][3];

    for (int i = 0; i < 3; i++) {
      board[i] = lines[i].split(",");
    }
    for (int k = 0; k < 3; k++) {
      for (int j = 0; j < 3; j++) {
        if (board[k][j].equals("1")) {
          intBoard[k][j] = 1;
        } else if (board[k][j].equals("-1")) {
          intBoard[k][j] = -1;
        } else {
          intBoard[k][j] = 0;
        }
      }
    }
    return intBoard;
  }

  // return
  // 0:  continue to play game
  // 1:  player 1 (using 'X') win
  // -1: player -1 (using 'O') win
  // 
  public int checkWinByTime() {
    if (this.timeUsed[0] > this.timeLimit) { // player using 'X' timeout
        return -1; // player using 'O' win
    } else if (this.timeUsed[1] > this.timeLimit) { // player using 'O' timeout
        return 1; // player using 'X' win
    } // no one timeout, checkWin on board
    return 0;
  }

  // return
  // 0:  continue to play game
  // 1:  player 1 win
  // -1: player -1 win
  // 2:  tie
  public int checkWin() {
    int winByTime = checkWinByTime();

    if (winByTime != 0) { // winer determined by time used.
        return winByTime;
    }

    for (int i = 0; i < 3; i++) { // check each row
      if ((this.board[i][0] == this.board[i][1] && 
           this.board[i][0] == this.board[i][2]) && 
           this.board[i][0] != 0) { return this.board[i][0]; }
    }
    for (int i = 0; i < 3; i++) { // check each col
      if ((this.board[0][i] == this.board[1][i] && 
           this.board[0][i] == this.board[2][i]) && 
           this.board[0][i] != 0) { return this.board[0][i]; }
    }
    if ((this.board[0][0] == this.board[1][1] && 
         this.board[0][0] == this.board[2][2]) && 
         this.board[0][0] != 0) { return this.board[0][0]; }
    if ((this.board[2][0] == this.board[1][1] && 
         this.board[2][0] == this.board[0][2]) && 
         this.board[2][0] != 0) { return this.board[2][0]; }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (this.board[i][j] == 0) { return 0; }
      }
    }
    return 2;
  } // checkwin

}
