package minesweeper;


import java.util.Random;

/**
 * Represents a mutable square in a game of minesweeper. Its coordinates are immutable.
 * Its state, either untouched, flagged or dug, can be changed.
 * A square also can or cannot contain a bomb, which can be specified by a chance or by explicitly setting it.
 */
public class Square {

    // Coordinates of the square, they stay fixed
    private final int x;
    private final int y;

    // State of the square, can be either untouched, flagged or dug
    private State state;
    private boolean containsBomb;

    public enum State{
        UNTOUCHED, FLAGGED, DUG
    }

    // Abstraction Function
    //      A square in a game of minesweeper is described by having a state as described by State, it further does or
    //      does not contain a bomb with a certain chance specified in the constructor.
    // Rep invariant
    //      None
    // Protection from rep exposure
    //      All the fields are private and immutable and can only changed by using a setter method
    // Thread Safety
    //      All access to its fields happens inside its methods and all
    //      these methods are guarded by Square's lock.

    /**
     * Make a square in a game of Minesweeper
     *
     * @param x               its x-coordinate
     * @param y               its y-coordinate (using standard CS-coordinates, so positive y is down)
     * @param percentageBombs chance of this square containing a bomb. When generating squares for a game of
     *                        minesweeper approx. this percentage of squares will contain a bomb.
     *                        Requires 0 <= {@code percentageBombs} <=1
     */
    public Square(int x, int y, double percentageBombs) {
        this.x = x;
        this.y = y;
        this.state = State.UNTOUCHED;
        assert (0 <= percentageBombs && percentageBombs <= 1);
        Random generator = new Random();
        this.containsBomb = (generator.nextDouble() <= percentageBombs);
    }

    /**
     * Make a square in a game of Minesweeper
     * @param x             its x-coordinate
     * @param y             its y-coordinate (using standard CS-coordinates, so positive y is down)
     * @param containsBomb  set true if the square has to contain a bomb and false otherwise
     */
    public Square(int x, int y, boolean containsBomb) {
        this(x, y, 0);
        this.containsBomb = containsBomb;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized boolean containsBomb() {
        return containsBomb;
    }

    public synchronized void setState(State state) {
        this.state = state;
    }

    public synchronized void setBomb(boolean containsBomb) {
        this.containsBomb = containsBomb;
    }
}
