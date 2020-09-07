/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A thread-safe minesweeper board for use on a multithreaded server. One can specify the dimensions in the constructor,
 * then 25% of the squares will be mines, or one can give the constructor a file with the layout of a board.
 * This board does not contain a win-condition, however when a mine is dug up it is removed so that one can keep
 * playing.
 *
 * See PS2 description for the precise specs.
 */
public class Board {

    // the dimensions of the board
    private final int width;
    private final int height;

    // chance of having a bomb in each square (must be between 0 and 1)
    private final double CHANCE_BOMBS = 0.25;

    // the board consists of a number of squares, here represented as a list of lists of squares
    private final List<List<Square>> squares;

    // to be displayed when a bomb is dug up
    private final String BOOM_message = "BOOM!\r";

    // Keep track of number of players
    private int numberPlayers = 0;

    // Abstraction function
    //      A board in a game of minesweeper is a rectangle (whose dimensions are specified by width and height)
    //      which consists out of a number of squares. Each square has it's own state (untouched, dug or flagged),
    //      and does or does not conatain a bomb. The averaged number of bombs can be governed by CHANCE_BOMBS.
    //      (See also the Square class for more about a square in a game of minesweeper)
    //      The coordinates of each square inside the rectangle are given by its indices inside this.squares.
    //
    // Rep invariant
    //      - number of players is always greater or equal to zero
    //      - the width and height are greater than zero
    //      - number of columns == width
    //      - number of elements of each column == height
    //      - the square at index (y,x) inside this.squares (i.e. squares.get(y).get(x)) has coordinate (x,y)
    //      - if a square is dug it cannot contain a bomb (this does not hold for the Square class itself however)
    //
    // Safety from rep exposure
    //      - Most of the fields are private and immutable and can only changed by using a setter method
    //      - The field squares is not immutable, but it is final, private and is never returned by any method.
    //        Only in the constructor are elements added to this field. By checking the rep invariant at appropriate
    //        times we make sure that it does not change in our methods. The elements of the elements of this field
    //        can be changed by using their setters, but this is in accordance to how the game works
    //
    // Thread safety
    //      - Monitor pattern: All access to the fields happens inside the methods of Board and all these
    //        methods are guarded by this Boards lock. This is sufficient since all fields are private.
    //      - The field squares is a synchronized list of synchronized lists, so all get and add methods
    //        on this field are atomic. The Square elements are thread-safe, so any number of mutations of
    //        such an element cannot happen concurrently, i.e. they are serialized. This combined with the
    //        fact that the mutation methods of this field are synchronized implies the thread safety of
    //        the field squares.

    /**
     * Create a minesweeper board with an already specified percentage of bombs (standard 0.25)
     * @param width width of the board, requires width > 0
     * @param height height of the board, requires height > 0;
     */
    public Board(final int width, final int height) {
        assert (width > 0 && height > 0);
        this.width = width;
        this.height = height;
        this.squares = Collections.synchronizedList(Collections.synchronizedList(new ArrayList<>()));
        for (int y = 0; y < height; y++) {
            this.squares.add(Collections.synchronizedList(new ArrayList<Square>()));
            for (int x = 0; x < width; x++) {
                this.squares.get(y).add(new Square(x, y, CHANCE_BOMBS));
            }
        }
        checkRep();
    }

    /**
     * Creates a square minesweeper board (see Board(int width, int height))
     * @param size the width and height of the board
     */
    public Board(final int size) {
        this(size, size);
    }

    /**
     * Creates a minesweeper board from a file
     * @param file the file containing (in text) a mineseeper board:<br>
     *      - the first line contains two integers separated by a space which define the width and height of the board <br>
     *      - the rest of the file consists only out of zeroes "0", ones "1" and whitespaces (spaces " " or newlines)
     *          The file has "height" rows. Each row then consists out "width" 0's or 1's, each number separated by
     *          a space. <p>
     * <b>Example:</b><br>
     *    6 4         <br>
     *    0 1 0 0 1 0 <br>
     *    1 0 1 1 0 0 <br>
     *    0 0 0 0 0 1 <br>
     *    0 1 0 0 1 0 <br>
     */
    public Board(File file) throws IOException {
        // Read the file and the first line
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readLine = bufferedReader.readLine();

        // Extract the dimensions of the board
        String[] dimensions = readLine.split("\\s");
        width = Integer.parseInt(dimensions[0]);
        height = Integer.parseInt(dimensions[1]);

        // make the list of lists of squares
        this.squares = Collections.synchronizedList(Collections.synchronizedList(new ArrayList<>()));

        // loop over all positions of the board as specified by the dimensions
        for (int y = 0; y<height; y++) {
            readLine = bufferedReader.readLine();
            String[] row = readLine.split("\\s");
            assert (row.length == width);
            this.squares.add(Collections.synchronizedList(new ArrayList<Square>()));
            for (int x = 0; x < width; x++) {
                this.squares.get(y).add(new Square(x, y, Integer.parseInt(row[x]) == 1));
            }
        }
        checkRep();
    }


    /**
     * Check the rep invariants
     */
    private synchronized void checkRep() {
        assert numberPlayers >= 0;
        assert (width > 0 && height > 0);
        assert squares.size() == height;
        for (int i = 0; i<height; i++) {
            assert squares.get(i).size() == width;
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Square square = this.squares.get(y).get(x);
                assert square.getX() == x;
                assert square.getY() == y;
                if (square.getState().equals(Square.State.DUG)) {
                    assert square.containsBomb() == false;
                }
            }
        }
    }


    /**
     * Use this method to draw the current state of the board
     * @return A sequence of dashes [-], numbers [1-8], whitespaces [ ], flags [F] and newlines [\n] representing
     * the state of this minesweeper board (see PS2 for precise specs) (no spaces at the end of the lines!).
     */
    public synchronized String look(){
        String board = "";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Square.State state = squares.get(y).get(x).getState();
                if (state == Square.State.FLAGGED) board += "F";
                else if (state == Square.State.UNTOUCHED) board += "-";
                else {
                    int numberBombs = countBombs(x, y);
                    if (numberBombs == 0) board += " ";
                    else board += Integer.toString(numberBombs);
                }
                // add whitespace between each square
                board += " ";
            }
            // remove last whitespace and add a newline for each new row
            board = board.substring(0, board.length() - 1);
            board += "\r\n";
        }
        // remove the last newline character '\n' since we use out.println
        board = board.substring(0, board.length() - 1);
        return board;
    }

    /**
     * Get a list of all the neighboring squares of a certain square (excludes the square itself)
     * This method has no thread-safety, only use in synchronized methods on instances of this class.
     * @param x the x-coordinate of the square, requires 0 <= x < width
     * @param y the y-coordinate of the square, requires 0 <= y < height
     */
    private List<Square> getNeighbors(int x, int y) {
        /* Since the dimensions of the board are fixed (they're final) this method does not need any synchronization
         * with other methods. The list it returns doesn't have to be synchronized since this method is private
         * and it's only accessed in synchronized methods. */
        List<Square> neighbors = new ArrayList<>();
        // We can find all neighbors of a square in a square grid by adding ±1 or 0 to its y and x coordinate
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                // Check that this square is indeed on the board, and include the square itself
                if (0 <= y + dy && y + dy < height &&
                        0 <= x + dx && x + dx < width &&
                        (dy != 0 || dx != 0)) {
                    neighbors.add(squares.get(y + dy).get(x + dx));
                }
            }
        }
        return neighbors;
    }

    /**
     * Count the number of bombs which neighbor to the square at coordinate (x,y) excluding itself
     */
    public synchronized int countBombs(int x, int y) {
        int numberBombs = 0;
        for (Square neighbor : getNeighbors(x, y)) {
            if (neighbor.containsBomb()) {
                numberBombs++;
            }
        }
        return numberBombs;
    }

    /**
     * If the square is untouched this flags that square, else it leaves the board unchanged
     * @param x the x-coordinate of the square, requires 0 <= x < width
     * @param y the y-coordinate of the square, requires 0 <= y < height
     * @return The current state of the board for drawing
     */
    public synchronized String flag(int x, int y){
        Square square = squares.get(y).get(x);
        if (square.getState() == Square.State.UNTOUCHED) {
            square.setState(Square.State.FLAGGED);
        }
        checkRep();
        return look();
    }

    /**
     * If the square is flagged this unflags that square, else it leaves the board unchanged
     * @param x the x-coordinate of the square, requires 0 <= x < width
     * @param y the y-coordinate of the square, requires 0 <= y < height
     * @return The current state of the board for drawing
     */
    public synchronized String deflag(int x, int y){
        Square square = squares.get(y).get(x);
        if (square.getState() == Square.State.FLAGGED) {
            square.setState(Square.State.UNTOUCHED);
        }
        checkRep();
        return look();
    }

    /**
     * Try to dig a certain square. If not 0 <= x < width or not 0 <= y < height, or if the square is not untouched
     * this just returns the current state of the board. Else it changes the state of the square to dug and:
     *  - If it isn't a bomb it reveals the square and if the square has no neighbors with bombs it reveals those
     *    too (same for those neighbors, recursively) and it returns the current state of the board.
     *  - If it is a bomb it also reveals the square as if there wasn't one, then the bomb is removed and it updates
     *    the squari, i.e. if it has no neighbors with bombs those neighbors will be revealed (again recursively).
     *    Then a BOOM! message is returned
     * @param x the x-coordinate of the square to be dug up
     * @param y the y-coordinate of the square to be dug up
     * @return A BOOM! message if there was a bomb, or else the state of the board
     */
    public synchronized String dig(int x, int y) {
        // Check if this square is a valid square to dig up
        if (validDig(x, y)) {

            // set the square to dug
            Square square = squares.get(y).get(x);
            square.setState(Square.State.DUG);
            boolean containedBomb = square.containsBomb();

            // If the square contained a bomb we remove it, uncover all the dug neighbors that have no neighbors with
            // bombs (including this square itself) and then send a BOOM message
            if (containedBomb) {
                square.setBomb(false);
                if (countBombs(x, y) == 0) uncoverAdjacents(x, y);
                checkRep();
                return BOOM_message;
            }

            // If this square has no neighbors that contain bombs, uncover them all
            if (countBombs(x, y) == 0) uncoverAdjacents(x, y);
        }
        checkRep();
        return look();
    }

    /**
     * Checks if 0 <= x < width and 0 <= y < height and if the square at this location is untouched, then it's a
     * valid square to dig
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return true if it's valid, otherwise false
     */
    private synchronized boolean validDig(int x, int y) {
        if (0 <= x && x < width && 0 <= y && y < height) {
            if (squares.get(y).get(x).getState() == Square.State.UNTOUCHED){
                return true;
            }
        }
        return false;
    }

    /**
     * This uncovers all neighboring squares of the square at the coordinate (x,y). This uses the so-called
     * flood-fill algorithm
     * Requires this square not to have any neighbors who contain a bomb
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    private synchronized void uncoverAdjacents(int x, int y) {
        assert countBombs(x, y) == 0;

        // Make a stack with all neighbors that are untouched
        Stack<Square> coveredNeighbors = new Stack<>();
        coveredNeighbors.addAll(getNeighbors(x, y).stream()
                .filter(c -> c.getState().equals(Square.State.UNTOUCHED))
                .collect(Collectors.toList()));

        // Take squares from the stack and dig them up
        while (!coveredNeighbors.isEmpty()) {
            Square neighbor = coveredNeighbors.pop();
            neighbor.setState(Square.State.DUG);

            // If this square also doesn't have any neighbors with bombs, uncover them all
            int xNeighbor = neighbor.getX();
            int yNeighbor = neighbor.getY();
            if (countBombs(xNeighbor, yNeighbor) == 0) {
                uncoverAdjacents(xNeighbor,yNeighbor);
            }
        }
        checkRep();
    }

//    public synchronized int getNumberPlayers() {
//        return numberPlayers;
//    }

    public synchronized void addPlayer() {
        numberPlayers++;
    }

    public synchronized void removePlayer() {
        numberPlayers--;
    }

//    public int getWidth() {
//        return width;
//    }
//
//    public int getHeight() {
//        return height;
//    }

    /**
     * Get a welcome message
     * @return A welcome message for new players joining the game, formatted as:
     * "Welcome to Minesweeper. Players: xx including you. Board: yy columns by zz rows. Type 'help' for help."
     */
    public synchronized String getWelcomeMessage(){
        String welcomeMessage = "Welcome to Minesweeper. Players: " + numberPlayers + " including you. ";
        welcomeMessage += "Board: " + width + " columns by " + height + " rows. ";
        welcomeMessage += "Type 'help' for help.\r";
        return welcomeMessage;
    }

    public String getBOOM_message() {
        return BOOM_message;
    }
}


//        Extra feature, apparently forbidden by the spec:
//         * An extra feature not clearly stated by the ProblemSet itself is that whenever a bomb is dug up not only that
//         * square itself is updated (i.e. if it has no neighbors with bombs then all neighbors are revealed) but also all
//         * its dug neighbors that, after removal of the bomb, have no neighbors with bombs left are updated too.
//        Replace part of the code in the dig method with:
//            if (containedBomb) {
//                    square.setBomb(false);
//                    if (countBombs(x, y) == 0) uncoverAdjacents(x, y);
//                    for (Square neighbor : getNeighbors(x, y)) {
//                        if (countBombs(neighbor.getX(), neighbor.getY()) == 0 &&
//                            neighbor.getState().equals(Square.State.DUG)) {
//                            uncoverAdjacents(neighbor.getX(), neighbor.getY());
//                        }
//                    }
//                    checkRep();
//                    return BOOM_message;
//            }
