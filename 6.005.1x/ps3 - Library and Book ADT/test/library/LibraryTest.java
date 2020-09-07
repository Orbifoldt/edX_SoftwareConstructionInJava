package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this BooleanSyntaxTree suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "library.SmallLibrary", 
            "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the BooleanSyntaxTree suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /*
     * Testing strategy
     * ==================
     *
     * Partition for all distinct states for each of the methods of Library:
     * - buy: library has the book, library hasn't got the book
     * - checkout: library has 1 copy of the book, or >1     (with that particular copy available by pre-condition)
     * - checkin: library has 1 copy of the book, or >1     (with that particular copy available by pre-condition)
     * - isAvailable: >=1 book available, 1 book not available, 1 book not in collection (i.e. never bought)
     * - allCopies: no books in collection, 1 book available, 1 book not available, >1 different available,
     *              >1 copies of 1 book (>1 of which available)
     * - availableCopies: no books in collection, 1 book available, 1 book not available, >1 different available,
     *                    >1 copies of 1 book (>1 of which available)
     * - find: exact match of either authors or title
     *         the query matches 0, 1 or >1 books
     *         library has 0, 1 or >1 copies (latter same or different years)
     *         in total there are no books, 1 book or >1 books
     * - lose: when library has 1 or >1 copies and the book is either lent or available
     *
     * Test cases buy:
     *  - see above
     *
     * Test cases checkout and checkin:
     *  - see above
     *
     * Test cases isAvailable and allCopies:
     *  - see above
     *
     * Test cases availableCopies:
     *  - see above
     *
     * Test cases find:
     *  - empty library
     *  - one book in library, search matches title, author, partial title, partial author and nothing
     *  - >1 books in library with >1 matches and a multiple copies
     *  - 4 copies of a book with the same title and author but different years, in random order added to library,
     *          and another book with less matches (checks date ordering and numberofmatches ordering)
     *
     * Test cases lose:
     *  - see above
     *
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    private static final Book book = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1997);


    @Test
    public void testBuy() {
        // Make an empty library and buy a book the library doesn't have
        Library library = makeLibrary();
        assertEquals(library.allCopies(book), Collections.emptySet()); // new library should be empty
        BookCopy copy1 = library.buy(book);
        assertTrue(library.isAvailable(copy1));  // the just bought book should be available

        // buy a book the library has
        BookCopy copy2 = library.buy(book);
        assertTrue(library.isAvailable(copy2));
    }

    @Test
    public void testCheckinAndCheckout() {
        // Make library, buy a book, checkout and check in
        Library library = makeLibrary();
        BookCopy copy1 = library.buy(book);
        assertTrue(library.isAvailable(copy1));
        library.checkout(copy1);
        assertFalse(library.isAvailable(copy1));
        library.checkin(copy1);
        assertTrue(library.isAvailable(copy1));

        // Buy another book and repeat
        BookCopy copy2 = library.buy(book);
        assertTrue(library.isAvailable(copy1));
        assertTrue(library.isAvailable(copy2));
        library.checkout(copy2);
        assertTrue(library.isAvailable(copy1));
        assertFalse(library.isAvailable(copy2));
        library.checkin(copy2);
        assertTrue(library.isAvailable(copy1));
        assertTrue(library.isAvailable(copy2));
    }

    @Test
    public void testIsAvailable() {
        Library library = makeLibrary();

        // no copies of the book in the library
        assertFalse(library.isAvailable(new BookCopy(book)));

        // copy available, then checked out, then checked in
        BookCopy copy = library.buy(book);
        assertTrue(library.isAvailable(copy));
        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
        library.checkin(copy);
        assertTrue(library.isAvailable(copy));
    }

    @Test
    public void testAllAndAvailableCopies() {
        Library library = makeLibrary();

        // no copies ever bought
        Set<BookCopy> emptyBookSet = Collections.emptySet();
        assertEquals(emptyBookSet, library.allCopies(book));
        assertEquals(emptyBookSet, library.availableCopies(book));

        // 1 copy bought and ...
        // ... 1 copy available
        BookCopy copy1 = library.buy(book);
        Set<BookCopy> oneCopySet = new HashSet<>(Arrays.asList(copy1));
        assertEquals(oneCopySet, library.allCopies(book));
        assertEquals(oneCopySet, library.availableCopies(book));
        // ... 1 copy unavailable
        library.checkout(copy1);
        assertEquals(emptyBookSet, library.availableCopies(book));
        assertEquals(oneCopySet, library.allCopies(book));

        // 2 copies bought and ...
        // ... 2 available
        BookCopy copy2 = library.buy(book);
        library.checkin(copy1);
        Set<BookCopy> twoCopiesSet = new HashSet<>(Arrays.asList(copy1, copy2));
        assertEquals(library.allCopies(book), twoCopiesSet);
        assertEquals(library.availableCopies(book), twoCopiesSet);
        // ... 1 available
        library.checkout(copy2);
        assertEquals(library.allCopies(book), twoCopiesSet);
        assertEquals(library.availableCopies(book), oneCopySet);
    }


    @Test
    public void testLose() {
        // Make library have two copies
        Library library = makeLibrary();
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);

        // lose a checked-out copy
        Set<BookCopy> oneCopySet = new HashSet<>(Arrays.asList(copy1));
        library.checkout(copy2);
        library.lose(copy2);
        assertEquals(oneCopySet, library.allCopies(book));

        // lose an available copy (so now no books are left)
        library.lose(copy1);
        assertFalse(library.isAvailable(copy1));
        assertTrue(library.allCopies(book).isEmpty());
    }


    @Test
    public void testFind() {
        // empty library should give no matches
        Library library = makeLibrary();
        assertTrue(library.find("Harry Potter").size() == 0);

        // one book in library, search matches title, author, partial title, partial author and nothing
        Book book1 = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        assertEquals(Arrays.asList(book1), library.find("Harry Potter"));
        assertEquals(Arrays.asList(book1), library.find("J.K. Rowling"));
        assertEquals(Arrays.asList(book1), library.find("Harry"));
        assertEquals(Arrays.asList(book1), library.find("Rowling"));
        assertTrue(library.find("Henk").size() == 0);

        // >1 books in library with >1 matches and a multiple copies
        Book book2 = new Book("Song of Ice and Fire", Arrays.asList("George R.R. Martin"), 1995);
        Book book3 = new Book("Game of Thrones", Arrays.asList("George R.R. Martin"), 1999);
        library.buy(book2);
        library.buy(book2);
        library.buy(book3);
        List<Book> searchResult = library.find("George R.R. Martin");
        assertTrue(searchResult.size() == 2); // book2 should only appear once
        assertEquals(new HashSet<>(searchResult), new HashSet<>(Arrays.asList(book2, book3))); // First convert the set!

        // now 4 copies of a book with the same title and author but different years, in random order added to library
        // and another book with less matches
        Book book1_2 = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1998);
        Book book1_3 = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 1999);
        Book book1_4 = new Book("Harry Potter", Arrays.asList("J.K. Rowling"), 2001);
        Book book4 = new Book("Harry Pasta", Arrays.asList("Fredje"), 2000);
        library.buy(book1_4);
        library.buy(book4);
        library.buy(book1_2);
        library.buy(book1_3);
        assertEquals(library.find("Harry Potter"), Arrays.asList(book1_4, book1_3, book1_2, book1,book4));
    }
    
    


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
