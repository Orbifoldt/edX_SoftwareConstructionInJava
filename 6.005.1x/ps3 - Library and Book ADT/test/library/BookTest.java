package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     *
     * A book has three fields, all should be immutable. The partition:
     * title - String:        1 letter and >1 letters
     * author - List<String>: 1 author, more authors
     *                        1 letter in name, more letters
     *                        1 word/name, more words/names
     *                        interpunction in names
     * year - int:            minimal year = 1, some year >1, year in future
     *
     * Author is case sensitive and authors list-order does matter
     *
     *
     * Test Cases for Book, getTitle, getAuthors, getYear and toString:
     *  - Minimal: title 1 letter, authors: 1 author 1 letter, year 1
     *  - NormalSimple: title multiple words and interpunction, one writer multiple names, year > 1
     *  - NormalMoreAuthors: title some words, multiple writers multiples names, year > 1
     *  - AuthorCapitalization: two books with same author but different capitalization, rest the same
     *  - AuthorsOrder: two books with same authors, but in different orders, rest the same
     *
     * Test Cases for equals and hashCode:
     *  - BooleanSyntaxTree that equals is an equivalence relation
     *  - BooleanSyntaxTree that if two objects are equal, then their hashcodes are equal
     *  - for non-null x, x.equals(null) = false
     *  - different titles gives false (but different capitalization probably shouldnt? this isnt specified so I wont BooleanSyntaxTree for this)
     *  - different names or different capitalizations give false
     *  - different years give false
     *
     * Test Cases Rep-invariance: (Only authors field is suceptible to mutation)
     *  - BooleanSyntaxTree mutation in constructor
     *  - BooleanSyntaxTree mutation in getAuthor
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    @Test
    public void testMinimal(){
        Book book = new Book("A", Arrays.asList("B"), 1);

        assertEquals(book.getTitle(), "A");
        assertEquals(book.getAuthors(), Arrays.asList("B"));
        assertEquals(book.getYear(), 1);
        assertTrue(book.toString().contains("A"));
        assertTrue(book.toString().contains("B"));
        assertTrue(book.toString().contains("1"));
    }

    @Test
    public void testNormalSimple(){
        Book book = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1997);

        assertEquals(book.getTitle(), "Harry Potter");
        assertEquals(book.getAuthors(), Arrays.asList("J.K. Rowling"));
        assertEquals(book.getYear(), 1997);
        assertTrue(book.toString().contains("Harry Potter"));
        assertTrue(book.toString().contains("J.K. Rowling"));
        assertTrue(book.toString().contains("1997"));
    }

    @Test
    public void testNormalMoreAuthors(){
        Book book = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);

        assertEquals(book.getTitle(), "Harry Potter");
        assertEquals(book.getAuthors(), Arrays.asList("J.K. Rowling","Gandalf"));
        assertEquals(book.getYear(), 1997);
        assertTrue(book.toString().contains("Harry Potter"));
        assertTrue(book.toString().contains("J.K. Rowling"));
        assertTrue(book.toString().contains("Gandalf"));
        assertTrue(book.toString().contains("1997"));
    }

    @Test
    public void testAuthorCapitalization(){
        Book book1 = new Book("Harry Potter", Arrays.asList("Rowling"), 1997);
        Book book2 = new Book("Harry Potter", Arrays.asList("ROwling"), 1997);

        assertEquals(book1.getAuthors(), Arrays.asList("Rowling"));
        assertEquals(book2.getAuthors(), Arrays.asList("ROwling"));
        assertFalse("Expected authors were different", book1.getAuthors().equals(book2.getAuthors()));
    }

    @Test
    public void testAuthorsOrder(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter", Arrays.asList("Gandalf", "J.K. Rowling"), 1997);

        assertEquals(book1.getAuthors(), Arrays.asList("J.K. Rowling", "Gandalf"));
        assertEquals(book2.getAuthors(), Arrays.asList("Gandalf", "J.K. Rowling"));
        assertFalse("Expected authors orders were different", book1.getAuthors().equals(book2.getAuthors()));
    }

    @Test
    public void testEqualsIsEquivalenceRelation(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book3 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);

        // Reflexivity
        assertEquals(book1,book1);
        // Symmetry
        assertEquals(book1, book2);
        assertEquals(book2, book1);
        // Transitivity
        assertEquals(book1, book2);
        assertEquals(book2, book3);
        assertEquals(book1,book3);
    }

    @Test
    public void testHashCodesEqual(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }


    @Test
    public void testNullEqualsFalse(){
        Book book = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        assertFalse(book.equals(null));
    }

    @Test
    public void testDifferentTitlesEqualsFalse(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter2", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        assertFalse(book1.equals(book2));
    }

    @Test
    public void testDifferentAuthorsEqualsFalse(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter2", Arrays.asList("J.K. Rowling","Gandalf2"), 1997);
        Book book3 = new Book("Harry Potter2", Arrays.asList("J.K. Rowling","GANDALF"), 1997);
        assertFalse(book1.equals(book2));
    }

    @Test
    public void testDifferentYearsEqualsFalse(){
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling","Gandalf"), 1997);
        Book book2 = new Book("Harry Potter2", Arrays.asList("J.K. Rowling","Gandalf"), 1998);
        assertFalse(book1.equals(book2));
    }

    @Test
    public void testRepInvarianceConstructor() {
        List<String> authors = new ArrayList<>();
        authors.add("Henk");
        Book book = new Book("Abc", authors, 2000);
        assertEquals(book.getAuthors(), Arrays.asList("Henk"));
        // Now mutate
        authors.add("Fred");
        assertEquals(book.getAuthors(), Arrays.asList("Henk"));
    }

    @Test
    public void testRepInvarianceGetAuthors() {
        Book book = new Book("Abc", Arrays.asList("Henk"), 2000);
        List<String> authors = book.getAuthors();
        assertEquals(authors, Arrays.asList("Henk"));
        // Now mutate
        authors.add("Fred");
        assertEquals(book.getAuthors(), Arrays.asList("Henk"));
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
