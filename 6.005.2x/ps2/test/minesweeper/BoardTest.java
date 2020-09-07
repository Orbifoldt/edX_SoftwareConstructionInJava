/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

/**
 * Tests for the Board class, these test the functionality of the game of multi-player minesweeper
 */
public class BoardTest {

    /*
    * ==================================== TESTING STRATEGY ====================================
    *
    * -------------- Partitions -------------------------------------
    * - possible states of a square: {untouched, dug, flagged} x {bomb, no bomb}
    * - possible local configurations, square can have as neighbors:
    *       - no bombs, 1 bomb, > 1 bombs but < 8, or all 8 bombs
    *       - any number 0-8 of untouched, flagged or dug neighbors
    *   and the square can be:
    *       - in the middle,
    *       - on the edge,
    *       - in the corner of the board
    *
    * ------------------------------------------------------------------------------------------------------------
    *
    * -------------- Specific board configurations used -------------
    * - Random board with about 25% bombs
    * - Completely empty board
    * - Board with relatively low amount of bombs, 10%
    * - Board with a bomb/square surrounded by only bombs
    *
    * ------------------------------------------------------------------------------------------------------------
    *
    * -------------- Tests for look method --------------------------
    * - Draw a board where nothing is revealed, i.e. it must be rows of consecutive dash-space combinations: "- "
    * - When a square is flagged it should be drawn as "F " instead of "- ", and when deflagged it should go back,
    *       Cases: - a single square flagged,
    *              - multiple squares flagged at the same time
    * - When a square not containing a bomb but having some neighbors with bombs it should display that number of bombs
    *       Cases:  - 1 neighboring bomb,
    *               - >1 neighboring bombs
    * - A square without any bomb-neighbors should be drawn as "  "
    *
    *
    * -------------- Tests for countBombs ---------------------------
    * - Count the bombs for a square
    *       Cases:  - in the middle,
    *               - on the edge,
    *               - in the corner of the board
    *
    *
    * -------------- Tests for flag and deflag ----------------------
    * - flag some square, try to flag it again, deflag it, try to deflag it again
    * - flag multiple squares
    * - flagged squares should never be dug by either standard digging or the flood-fill digging
    *
    *
    * -------------- Tests for dig ----------------------------------
    * - digging a square neighboring some bombs returns the correct state of the game with numbers on dug places
    * - Digging a flagged square (both bomb and no bomb) does not mutate the board and returns the state of the board
    * - digging a square without any neighbors with bombs should uncover those too recursively
    *       Cases: - empty board,
    *              - nearly empty board,
    *              - empty board with 2 flags then deflag one and dig it
    * - digging a bomb should send BOOM! message and update all the neighboring squares (open all that have
    *   zero bomb-neighbors) when appropriate:
    *       Cases: - bomb without any bomb-neighbors and with neighbors also not having bomb-neighbors,
    *              - bomb with bomb-neighbors but with some neighbors that don't have bomb-neighbors,
    *              - bomb with 8 bomb-neighbors all around,
    *              - bomb with some bomb neighbors and all its neighbors do have >=1 bomb-neighbors
    */


    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // Some test files:

    //    testBoard: 7/24 bombs
    //    6 4
    //    0 1 0 0 1 0
    //    1 0 1 1 0 0
    //    0 0 0 0 0 1
    //    0 0 0 0 1 0
    private final File file = new File("./test/minesweeper/testBoards/testBoard");

    //    emptyBoard: 0/30 bombs
    //    6 5
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    private final File empty = new File("./test/minesweeper/testBoards/emptyBoard");

    //    nearlyEmptyBoard: 3/30 bombs
    //    6 5
    //    0 0 0 1 0 1
    //    0 0 0 0 0 0
    //    0 0 0 0 0 0
    //    0 0 0 0 1 0
    //    0 0 0 0 0 0
    private final File nearlyEmpty = new File("./test/minesweeper/testBoards/nearlyEmptyBoard");


    //    oneBombSurroundedByBombs: 9/30 bombs
    //    6 5
    //    0 0 0 0 0 0
    //    0 1 1 1 0 0
    //    0 1 1 1 0 0
    //    0 1 1 1 0 0
    //    0 0 0 0 0 0
    private final File oneBombSurroundedByBombs = new File("./test/minesweeper/testBoards/oneBombSurroundedByBombs");



    @Test
    public void testDrawNothingRevealed() throws IOException {
        Board board = new Board(file);

        // look the board, nothing should be revealed
        String boardString = board.look();
        for (int i = 0; i < 4; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                   
                    "- - - - - -");
        }
    }

    @Test
    public void testCountBombs() throws IOException{
        Board board = new Board(file);
        // countBombs in middle
        assertEquals(3,board.countBombs(4,1));
        // countBombs on edge
        assertEquals(1, board.countBombs(3,3));
        assertEquals(0, board.countBombs(1,4));
        // countBombs in corner
        assertEquals(2, board.countBombs(5,3));
    }

    @Test
    public void testFlag() throws IOException {
        Board board = new Board(file);

        // Flag the square at 2,1
        String boardString = board.flag(2, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                     "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                     "- - F - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                     "- - - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Try to flag it again
        boardString = board.flag(2, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - F - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Deflag it
        boardString = board.deflag(2, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Try to deflag it again
        boardString = board.deflag(2, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Flag multiple squares
        board.flag(4, 1);
        board.flag(3, 3);
        board.flag(5, 0);
        board.flag(0, 2);
        board.flag(1, 2);
        boardString = board.flag(2, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - F");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - F - F -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "F F - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - F - -");
    }

    @Test
    public void testDigFlagsAndNoBombs() throws IOException{
        Board nearlyEmptyBoard = new Board(nearlyEmpty);

        // look the board, nothing should be revealed yet
        String boardString = nearlyEmptyBoard.look();
        for (int i = 0; i < 5; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Dig a square up
        boardString = nearlyEmptyBoard.dig(4, 0);
        assertEquals("- - - - 2 -", boardString.split("\n")[0]
                                                        .replaceAll("(\\r\\n|\\r|\\n)", ""));
        for (int i = 1; i < 5; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Dig more squares up
        nearlyEmptyBoard.dig(2, 0);
        nearlyEmptyBoard.dig(5, 1);
        nearlyEmptyBoard.dig(3, 1);
        nearlyEmptyBoard.dig(2, 1);
        boardString = nearlyEmptyBoard.dig(4, 1);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 - 2 -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 1 2 1");
        for (int i = 2; i < 5; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Flag the bombs
        nearlyEmptyBoard.flag(3, 0);
        boardString = nearlyEmptyBoard.flag(5, 0);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 F 2 F");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 1 2 1");
        for (int i = 2; i < 5; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Try to dig a flagged square
        boardString = nearlyEmptyBoard.dig(5, 0);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 F 2 F");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 1 1 2 1");
        for (int i = 2; i < 5; i++) {
            assertEquals(boardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }
    }

    @Test
    public void testDigUncoversNeighbors() throws IOException{
        Board emptyBoard = new Board(empty);

        // look the board, nothing should be revealed yet
        String emptyBoardString = emptyBoard.look();
        for (int i = 0; i < 5; i++) {
            assertEquals(emptyBoardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Now dig in some square, all should be revealed
        emptyBoardString = emptyBoard.dig(1,4);
        for (int i = 0; i < 5; i++) {
            assertEquals(emptyBoardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        }

        // dig a square with some neighbors without bomb-neighbors
        Board nearlyEmptyBoard = new Board(nearlyEmpty);
        String boardString = nearlyEmptyBoard.dig(2, 4);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    1 - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    1 1 - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      1 - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      1 - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      1 - -");
    }

    @Test
    public void testDigUncoversNeighborsButLeavesFlags() throws IOException{
        Board emptyBoard = new Board(empty);

        // look the board, nothing should be revealed yet
        String emptyBoardString = emptyBoard.look();
        for (int i = 0; i < 5; i++) {
            assertEquals(emptyBoardString.split("\n")[i].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        }

        // Flag some squares, and dig somewhere else, then all but the flags should reveal
        emptyBoard.flag(2, 2);
        emptyBoard.flag(3, 3);
        emptyBoardString = emptyBoard.dig(1,4);
        assertEquals(emptyBoardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    F      ");
        assertEquals(emptyBoardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      F    ");
        assertEquals(emptyBoardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");

        // deflag one of the flags
        emptyBoardString = emptyBoard.deflag(2, 2);
        assertEquals(emptyBoardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    -      ");
        assertEquals(emptyBoardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      F    ");
        assertEquals(emptyBoardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");

        // dig it up
        emptyBoardString = emptyBoard.dig(2, 2);
        assertEquals(emptyBoardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(emptyBoardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "      F    ");
        assertEquals(emptyBoardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
    }

    @Test
    public void testDigBombsAndFloodFill() throws IOException{
        Board nearlyEmptyBoard = new Board(nearlyEmpty);

        // Dig some squares up and flag a bomb
        nearlyEmptyBoard.dig(4, 0);
        nearlyEmptyBoard.dig(2, 0);
        nearlyEmptyBoard.dig(5, 1);
        nearlyEmptyBoard.dig(3, 1);
        nearlyEmptyBoard.dig(2, 1);
        nearlyEmptyBoard.dig(4, 1);
        nearlyEmptyBoard.flag(5, 0);

        // Now dig up a bomb
        String boardString = nearlyEmptyBoard.dig(3, 0);
        assertTrue(boardString.contains("BOOM!"));

        // The bomb should be removed, and the rest of the board should be updated
        boardString = nearlyEmptyBoard.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- -     1 F");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- -     1 1");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        // Alternative spec where also all dug neighbors of the bomb are updated
//        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
//                    "        1 F");
//        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
//                    "        1 1");
//        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
//                    "      1 1 -");
//        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
//                    "      1 - -");
//        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
//                    "      1 - -");

        // Reset and dig the bomb that has no bombs as neighbors
        nearlyEmptyBoard = new Board(nearlyEmpty);
        assertTrue(nearlyEmptyBoard.dig(4, 3).contains("BOOM!"));
        boardString = nearlyEmptyBoard.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    1 - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "    1 1 2 1");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "           ");
    }

    @Test
    public void testDigBombAllNeighborsBombs() throws IOException{
        Board board = new Board(oneBombSurroundedByBombs);

        // Now dig up the middle bomb
        String boardString = board.dig(2, 2);
        assertTrue(boardString.contains("BOOM!"));
        boardString = board.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 8 - - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Now dig up a bomb to the right
        boardString = board.dig(3, 2);
        assertTrue(boardString.contains("BOOM!"));
        boardString = board.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 7 4 - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // dig up another bomb
        boardString = board.dig(2, 1);
        assertTrue(boardString.contains("BOOM!"));
        boardString = board.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 3 - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 6 3 - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // dig up yet another bomb
        boardString = board.dig(1, 2);
        assertTrue(boardString.contains("BOOM!"));
        boardString = board.look();
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 2 - - -");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- 3 5 3 - -");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - - -");

        // Dig up a square without a bomb
        boardString = board.dig(5, 2);
        assertEquals(boardString.split("\n")[0].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - 1  ");
        assertEquals(boardString.split("\n")[1].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - 2 - 1  ");
        assertEquals(boardString.split("\n")[2].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- 3 5 3 2  ");
        assertEquals(boardString.split("\n")[3].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - 1  ");
        assertEquals(boardString.split("\n")[4].replaceAll("(\\r\\n|\\r|\\n)", ""),
                    "- - - - 1  ");
    }

}
