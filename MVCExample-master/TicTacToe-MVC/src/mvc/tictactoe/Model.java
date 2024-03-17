package mvc.tictactoe;

import com.mrjaffesclass.apcs.messenger.*;

import javax.swing.JButton;

/**
 * The model represents the data that the app uses.
 *
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

    // Messaging system for the MVC
    private final Messenger mvcMessaging;

    // Model's data variables
    private boolean whoseMove;
    private boolean gameOver;
    private String[][] board;
    private int count = 0;

    /**
     * Model constructor: Create the data representation of the program
     *
     * @param messages Messaging class instantiated by the Controller for local
     * messages between Model, View, and controller
     */
    public Model(Messenger messages) {
        mvcMessaging = messages;
        this.board = new String[3][3];
    }

    /**
     * Initialize the model here and subscribe to any required messages
     */
    public void init() {
        this.newGame();
        this.mvcMessaging.subscribe("playerMove", this);
        this.mvcMessaging.subscribe("newGame", this);

    }

    /**
     * Reset the state for a new game
     */
    private void newGame() {
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                this.board[row][col] = "";
            }
        }
        count = 0;
        this.whoseMove = false;
        this.gameOver = false;
    }

    private String isWinner() {
        // Check the rows and columns for a tic tac toe
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) && !board[i][0].equals("")) {
                return board[i][0];
            }
            if (board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]) && !board[0][i].equals("")) {
                return board[0][i];
            }
        }

        // Check the diagonals
        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            return board[0][0];
        }
        if (board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            return board[0][2];
        }

        // If we haven't found it, then return a blank string
        return "";
    }
    
    public boolean gameTied() {
        if (count == 9) {
            return gameOver = true;
        }
        return gameOver;
    }

    @Override
    public void messageHandler(String messageName, Object messagePayload) {
        // Display the message to the console for debugging
        if (messagePayload != null) {
            System.out.println("MSG: received by model: " + messageName + " | " + messagePayload.toString());
        } else {
            System.out.println("MSG: received by model: " + messageName + " | No data sent");
        }

        // playerMove message handler
        if (messageName.equals("playerMove") && !gameTied()) {
            // Get the position string and convert to row and col
            String position = (String) messagePayload;
            Integer row = new Integer(position.substring(0, 1));
            Integer col = new Integer(position.substring(1, 2));
            // If square is blank...
            if (this.board[row][col].equals("")) {
                // ... then set X or O depending on whose move it is
                if (this.whoseMove) {
                    this.board[row][col] = "X";
                } else {
                    this.board[row][col] = "O";
                }
                count++;
                whoseMove = !whoseMove;
                // Send the boardChange message along with the new board 
                this.mvcMessaging.notify("boardChange", this.board);
            }
        }
        
        if (this.isWinner().equals("X")) {
            this.mvcMessaging.notify("xWins", this.board);
        }
        
        if (this.isWinner().equals("O")) {
            this.mvcMessaging.notify("oWins", this.board);
        }
        
        if (gameTied()) {
            this.mvcMessaging.notify("gameTied", this.board);
        }

// newGame message handler
        if (messageName.equals("newGame")) {
            // Reset the app state
            this.newGame();
            // Send the boardChange message along with the new board 
            this.mvcMessaging.notify("boardChange", this.board);
        }

    }

}
