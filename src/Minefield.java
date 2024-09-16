

import java.awt.*;
import java.util.Queue;
import java.util.Random;

public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";

    private int rows;
    private int columns;
    private int flags;
    private Cell[][] board;
    private int mines;

    private boolean firstMove = true;
    private boolean gameOver = false;
    private boolean debugMode;






    /*
     * Class Variable Section
     *
    */

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */


    /**
     * Minefield
     *
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */

    public Minefield(int rows, int columns, int flags, boolean debugMode) { // creates new minefield with desired rows and columns
        this.rows = rows;
        this.columns = columns;
        this.flags = flags;
        board = new Cell[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                board[i][j] = new Cell(false, "-");
            }
        }
        this.mines = flags;
        this.debugMode = debugMode;
    }

    /**
     * evaluateField
     *
     *
     * @function:
     * Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     *
     */
    public void evaluateField() { // iterates through all spaces on board to check
        for (int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(board[i][j].getStatus().equals("-")){
                    int mineCounter = 0;
                    for(int x = -1; x < 2; x++){ // checks adjacent spaces for mines
                        for(int y = -1; y < 2; y++){
                            if(i+x >= 0 && j+y >= 0 && i+x < rows && y+j < columns) {
                                if (board[i + x][j + y].getStatus().equals("M")) {
                                    mineCounter++;
                                }
                            }
                         }
                    }
                    board[i][j].setStatus(""+ mineCounter); // updates the status of board with amount of mines
                }
            }

        }

    }

    /**
     * createMines
     *
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) { // creates mines on minefield
        Random rand = new Random();
        for(int i = 0; i < mines; i++){

            int randX = rand.nextInt(this.rows);
            int randY = rand.nextInt(this.columns);
            if((x != randX && y != randY) && (!board[randX][randY].getStatus().equals("M"))){ // setting random space to mine
                board[randX][randY].setStatus("M");
            }
            else{ // if space already mine then skip
                i--;
            }

        }

    }

    /**
     * guess
     *
     * Check if the guessed cell is inbounds (if not done in the Main class).
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     *
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        if(x > rows-1 || y > columns-1 || x < 0 || y < 0){ // checks for out of bounds guess
            System.out.println("Guess is out of bounds.");
            return false;
        } else {
            if(firstMove){ // sets up board if first move
                createMines(x,y,mines);
                evaluateField();
                if(board[x][y].getStatus().equals("0")){ // if guess is 0 then reveals surrouding zeros
                    revealZeroes(x,y);
                }
                revealStartingArea(x,y); // reveals starting area around guess
                firstMove = false;
            } else if(board[x][y].getStatus().equals("M")) { // checks if guess is on mine
                board[x][y].setRevealed(true);
                if(flag) { // places flag if user decides to flag
                    board[x][y].setStatus("F");
                    flags--;
                    mines--;
                    if(mines == 0){ // checks if all mines have been flagged
                        gameOver = true;
                    }
                }
                else {
                    gameOver = true;
                }
            }
            else if(!board[x][y].getStatus().equals("M")){
                if(board[x][y].getStatus().equals("0")) {
                    revealZeroes(x,y);
                }
                if(flag) {
                    board[x][y].setStatus("F");
                    flags--;
                }
                board[x][y].setRevealed(true);
            }
            return true;
        }
    }

    /**
     * gameOver
     *
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        Stack1Gen stack = new Stack1Gen(); // creates new stack to hold coordinates of cells
        int[] points = {x,y};
        stack.push(points);
        while(!stack.isEmpty()){ // checks and processes all cells within stack
            int[] curr = (int[]) stack.pop();
            board[curr[0]][curr[1]].setRevealed(true);
            if (curr[0] + 1 <= rows-1 && board[curr[0]+1][curr[1]].getStatus().equals("0") && board[curr[0]+1][curr[1]].getRevealed() == false) {
                int[] addDown = {curr[0] + 1,curr[1]};
                stack.push(addDown);
            }
            if (curr[0] - 1 >= 0 && board[curr[0]-1][curr[1]].getStatus().equals("0") && board[curr[0]-1][curr[1]].getRevealed() == false) {
                int[] addUp = {curr[0] - 1,curr[1]};
                stack.push(addUp);
            }
            if (curr[1] + 1 <= columns-1 && board[curr[0]][curr[1]+1].getStatus().equals("0") && board[curr[0]][curr[1]+1].getRevealed() == false) {
                int[] addRight = {curr[0],curr[1] + 1};
                stack.push(addRight);
            }
            if (curr[1] - 1 >= 0 && board[curr[0]][curr[1]-1].getStatus().equals("0") && board[curr[0]][curr[1]-1].getRevealed() == false) {
                int[] addLeft = {curr[0],curr[1] - 1};
                stack.push(addLeft);
            }
        }




    }

    /**
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the inital cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        Q1Gen<Point> queue = new Q1Gen<Point>(); // Create a queue to hold the coordinates of cells to be processed

        // Mark the starting cell as revealed and add it to the queue
        board[x][y].setRevealed(true);
        Point loc = new Point(x,y);
        queue.add(loc);

        while(queue.length() != 0) { // go through cells until the queue is empty

            Point curr = queue.remove(); // Remove the coordinates of the current cell from the queue
            board[curr.x][curr.y].setRevealed(true); // Mark the current cell as revealed

            if(board[curr.x][curr.y].getStatus().equals("M")) { // If the current cell is a mine, stop revealing the area
                return;
            }

            // Add adjacent cells to the queue to be checked if they haven't been revealed yet
            if(curr.x-1 >= 0) {
                Point p = new Point(curr.x-1,curr.y);
                if(!board[p.x][p.y].getRevealed()) {
                    queue.add(p);
                }
            }
            if(curr.x+1 < columns) {
                Point p = new Point(curr.x+1,curr.y);
                if(!board[p.x][p.y].getRevealed()) {
                    queue.add(p);
                }
            }
            if(curr.y-1 >= 0) {
                Point p = new Point(curr.x,curr.y-1);
                if(!board[p.x][p.y].getRevealed()) {
                    queue.add(p);
                }
            }
            if(curr.y+1 < rows) {
                Point p = new Point(curr.x,curr.y+1);
                if(!board[p.x][p.y].getRevealed()) {
                    queue.add(p);
                }
            }



        }

    }

    /**
     * For both printing methods utilize the ANSI colour codes provided!
     *
     *
     *
     *
     *
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void debug() {
        System.out.println("DEBUGGING VIEW:");
        System.out.print("-");
        for(int i = 0; i < columns; i++) {
            System.out.print("---");
        }
        System.out.print("\n");

        System.out.print("   ");
        for(int i = 0; i < columns; i++) {
            if(i < 10) {
                System.out.print(i + "  ");
            }else {
                System.out.print(i + " ");
            }
        }
        System.out.print("\n");

        for(int i = 0; i < rows; i++) {
            if(i < 10) {
                System.out.print(i + "  ");
            }else {
                System.out.print(i + " ");
            }
            for(int j = 0; j < columns; j++) {
                System.out.print(color(board[i][j].getStatus()) + "  ");
            }
            System.out.println();
        }

        System.out.print("-");
        for(int i = 0; i < columns; i++) {
            System.out.print("---");
        }
        System.out.print("\n");

        System.out.println();
    }



    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() { // string representation of minefield
        if(debugMode) {
            debug();
        }
        String str = "";

        str += "MINEFIELD:\n";

        str += "-";
        for(int i = 0; i < columns; i++) {
            str += "---";
        }
        str += "\n";

        str += "   ";
        for(int i = 0; i < columns; i++) {
            if(i < 10) {
                str += i + "  ";
            } else {
                str += i + " ";
            }
        }
        str += "\n";

        for(int i = 0; i < rows; i++) {
            if(i < 10) {
                str += i + "  ";
            }else {
                str += i + " ";
            }
            for(int j = 0; j < columns; j++) {
                if(board[i][j].getRevealed()) {
                    str += color(board[i][j].getStatus()) + "  ";
                } else {
                    str += "-  ";
                }
            }
            str += "\n";
        }

        str += "-";
        for(int i = 0; i < columns; i++) {
            str += "---";
        }
        str += "\n";

        return str;
    }
    public String color(String input) {
        switch(input) {
            case "-":
                return ANSI_GREY_BACKGROUND + input;
            case "M":
                return ANSI_RED + input + ANSI_GREY_BACKGROUND;
            case "4":
                return ANSI_RED + input + ANSI_GREY_BACKGROUND;
            case "3":
                return ANSI_PURPLE + input + ANSI_GREY_BACKGROUND;
            case "2":
                return ANSI_GREEN + input + ANSI_GREY_BACKGROUND;
            case "1":
                return ANSI_CYAN + input + ANSI_GREY_BACKGROUND;
            case "0":
                return ANSI_YELLOW + input + ANSI_GREY_BACKGROUND;
            default:
                return ANSI_RED + input + ANSI_GREY_BACKGROUND;

        }
    }

    public int getMines(){
        return mines;
    }
    public int getFlags(){
        return flags;
    }
}
