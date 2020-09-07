package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     *
     * A BookCopy object has two fields:
     * book - Book:             No need for particular books
     * conditions - Condition:  GOOD or DAMAGED
     *
     * Tests for getBook, getCondition, setCondition and to String
     *  - GetBook: BooleanSyntaxTree that the getBook equals the book with which we defined the book
     *  - Condition: BooleanSyntaxTree that the defined condition is equal to the actual condition (tests both good and damaged)
     *  - ToString: BooleanSyntaxTree toString contains good and bad resp.
     *
     * We don't have to BooleanSyntaxTree equals and hashCode since we are using the standard Object implementation of these
     */
    
    // TODO: put JUnit @Test methods here that you developed from your testing strategy



    private static final Book book = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1997);


    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    @Test
    public void testGetBook() {
        BookCopy bookCopy = new BookCopy(book);
        assertEquals(bookCopy.getBook(), book);
    }

    @Test
    public void testCondition() {
        BookCopy bookCopy = new BookCopy(book);
        assertEquals(bookCopy.getCondition(), BookCopy.Condition.GOOD);
        // Set to damaged
        bookCopy.setCondition(BookCopy.Condition.DAMAGED);
        assertEquals(bookCopy.getCondition(), BookCopy.Condition.DAMAGED);
        // Set back to good
        bookCopy.setCondition(BookCopy.Condition.GOOD);
        assertEquals(bookCopy.getCondition(), BookCopy.Condition.GOOD);
    }

    @Test
    public void testToString(){
        BookCopy bookCopy = new BookCopy(book);
        assertTrue(bookCopy.toString().contains("good"));
        // Set to damaged
        bookCopy.setCondition(BookCopy.Condition.DAMAGED);
        assertTrue(bookCopy.toString().contains("damaged"));
        // Set back to good
        bookCopy.setCondition(BookCopy.Condition.GOOD);
        assertTrue(bookCopy.toString().contains("good"));
    }



    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
