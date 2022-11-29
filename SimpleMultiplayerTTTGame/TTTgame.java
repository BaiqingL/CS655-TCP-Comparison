// simple tic tac toe

import java.io.*;
import java.util.*;
import java.lang.*;

public class TTTgame {
  public volatile int playerMove; //X = 1, O = -1
  private int[][] board; //3x3

  public TTTgame() {
    this.board = new int[3][3];
    this.playerMove = 1;
  }

  public void resetGame() {
    this.board = new int[3][3];
    this.playerMove = 1;
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

  public String printState() {
    String output = "#";
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 2; j++) {
         output += Integer.toString(this.board[i][j]) + ",";
      }
      output += Integer.toString(this.board[i][2]) + ";";
    }
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
  }

  // return
  // 0:  continue to play game
  // 1:  player 1 win
  // -1: player -1 win
  // 2:  tie
  public int checkWin() {
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
