import java.util.Arrays;
import java.util.Spliterator;

public class TicTacToeGame {

    private static final char PLAYERX = 'X';     // Helper constant for X player
    private static final char PLAYERO = 'O';     // Helper constant for O player
    private static final char SPACE = ' ';       // Helper constant for spaces
    private char[][] board;
    private int size = 3;
    private static char currentPlayer = PLAYERO;
    private boolean xWon = false;
    private boolean yWon = false;
    private int anInt;


    /*
    Sample TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */

    // TODO 4: Implement necessary methods to manage the games of Tic Tac Toe
    /*
        TODO:
            >fix the print screen for spaces

            >second icon doesn't print
            >recipient doesn't get the game
            >not right index
            >two people cant go at the same time

            >make sure you connect this to the server and client side so someone from 2 clients can place their x/o
    */

    /*
            What i did:
                >place the playerX and playerY, make sure x or o can’t go twice
                >i set up methods to check if a player won
                    >if ANY player got horizontal
                    >if ANY player got vertical
                    >if ANY player got diagnol
                  > it should be noted that i dont know if it works or not lol.
                    >you might have to change up the bounds if anything, if it doesnt work
                >the last method checkIfPersonWon basically checks if any of the boolean methods for winning are true.
                    if they are, then it will return true
                >i have a isFull method so you can check if the board is full or not.
                    >when you do a place X/O, make sure you do this first


    */


    public TicTacToeGame(String otherPlayer) {
    this.board = new char[size][size];
   // this.anInt = parameter;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = SPACE;
            }
        }
//        otherPlayer = ;

    }

    public TicTacToeGame(String otherPlayer, int anInt) {
        this.anInt = anInt;
        this.board = new char[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = SPACE;
            }
        }
    }

    public char[][] getBoard() {
        return board.clone();
    }

    public void printScreen() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                System.out.print(board[i][j] + "  |");
            }
            System.out.println();
            System.out.println("-----------");
        }
    }

    public boolean getSpace(int anInt){
        if (anInt == 0){
            return board[0][0] == SPACE;
        }
        else if (anInt == 1){
            return board[0][1] == SPACE;
        }
        else if (anInt == 2){
            return board[0][2] == SPACE;
        }
        else if (anInt == 3){
            return board[1][0] == SPACE;
        }
        else if (anInt == 4){
            return board[1][1] == SPACE;
        }
        else if (anInt == 5){
            return board[1][2] == SPACE;
        }
        else if (anInt == 6){
            return board[2][0] == SPACE;
        }
        else if (anInt == 7){
            return board[2][1] == SPACE;
        }
        else  {
            return board[2][2] == SPACE;
        }

    }

    public boolean isFull(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == SPACE)
                    return false;
            }
        }
        System.out.println("The board is full dummy");
        return true;
    }

    public boolean isTied() {
        if (isFull() && !checkIfPersonWon()) {
            System.out.println("Oh no!! Game ended in a tie!!");
            return true;
        }
        else {
            return false;
        }

    }

    public void checkPerson(){
        if (currentPlayer == PLAYERX)
            currentPlayer = PLAYERO;
        else
            currentPlayer = PLAYERX;
    }

    public void placeCharacter(int anInt){
//       while (!isFull()) {
            if (isTied())
                return;
            else if (getSpace(anInt)){
                checkPerson();
                if (anInt == 0) {
                    board[0][0] = currentPlayer;
                } else if (anInt == 1) {
                    board[0][1] = currentPlayer;
                } else if (anInt == 2) {
                    board[0][2] = currentPlayer;
                } else if (anInt == 3) {
                    board[1][0] = currentPlayer;
                } else if (anInt == 4) {
                    board[1][1] = currentPlayer;
                } else if (anInt == 5) {
                    board[1][2] = currentPlayer;
                } else if (anInt == 6) {
                    board[2][0] = currentPlayer;
                } else if (anInt == 7) {
                    board[2][1] = currentPlayer;
                } else {
                    board[2][2] = currentPlayer;
                }
            }
            else
               System.out.println("The index is taken already! You blind bro?");
//        }
    }

    public boolean checkColumnsForWin(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != SPACE && board[i][j] == board[i + 1][j] &&
                        board[i + 1][j] == board[i + 2][j]){
                    if (board[i][j] == PLAYERX)
                        xWon = true;
                    else if (board[i][j] == PLAYERO)
                        yWon = true;

                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkRowsForWin(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != SPACE && board[i][j] == board[i][j + 1] &&
                        board[i][j + 1] == board[i][j + 2]){
                    if (board[i][j] == PLAYERX)
                        xWon = true;
                    else if (board[i][j] == PLAYERO)
                        yWon = true;

                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkDiagnolsForWin(){
//        checking for negative slope
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != SPACE && board[i][j] == board[i + 1][j + 1] &&
                        board[i + 1][j + 1] == board[i + 2][j + 2]){
                    if (board[i][j] == PLAYERX)
                        xWon = true;
                    else if (board[i][j] == PLAYERO)
                        yWon = true;

                    return true;
                }
            }
        }
//        checking for positive slope
        for (int i = size - 1; i >= 0 ; i--) {
            for (int j = 0; j < size ; j++) {
                if (board[i][j] != SPACE && board[i][j] == board[i - 1][j + 1] &&
                        board[i - 1][j + 1] == board[i - 2][j + 2]){
                    if (board[i][j] == PLAYERX)
                        xWon = true;
                    else if (board[i][j] == PLAYERO)
                        yWon = true;

                    return true;
                }

            }
        }
        return false;
    }

    public boolean checkIfPersonWon(){
        if (checkColumnsForWin() || checkRowsForWin() || checkDiagnolsForWin()) {
            if (xWon)
                System.out.println("Player X Won!!!");
            else if (yWon)
                System.out.println("Player O Won!!!");
            return true;
        } else
            return false;
    }


}