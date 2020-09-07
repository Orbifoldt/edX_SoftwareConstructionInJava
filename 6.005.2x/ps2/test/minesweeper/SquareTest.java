package minesweeper;

import org.junit.Test;

import static org.junit.Assert.*;

public class SquareTest {

    private final Square square = new Square(2, 0, 0.5);
    private final Square squareWithBomb = new Square(4,1,true);
    private final Square squareWithoutBomb = new Square(5,3,false);

    @Test
    public void getX() throws Exception {
        assertEquals(2,square.getX());
    }

    @Test
    public void getY() throws Exception {
        assertEquals(0,square.getY());
    }

    @Test
    public void getState() throws Exception {
        assertEquals(Square.State.UNTOUCHED, square.getState());
        assertEquals(Square.State.UNTOUCHED, squareWithBomb.getState());
        assertEquals(Square.State.UNTOUCHED, squareWithBomb.getState());
    }

    @Test
    public void containsBomb() throws Exception {
        assertTrue(squareWithBomb.containsBomb());
        assertFalse(squareWithoutBomb.containsBomb());
    }

    @Test
    public void setState() throws Exception {
        final Square square1 = new Square(0, 2, 0.5);
        assertEquals(Square.State.UNTOUCHED, square1.getState());

        square1.setState(Square.State.DUG);
        assertEquals(Square.State.DUG, square1.getState());
        square1.setState(Square.State.UNTOUCHED);
        assertEquals(Square.State.UNTOUCHED, square1.getState());
        square1.setState(Square.State.FLAGGED);
        assertEquals(Square.State.FLAGGED, square1.getState());
    }

    @Test
    public void setBomb() throws Exception {
        final Square square2 = new Square(0, 2, true);
        assertTrue(square2.containsBomb());
        square2.setBomb(false);
        assertFalse(square2.containsBomb());
        square2.setBomb(true);
        assertTrue(square2.containsBomb());
    }

}