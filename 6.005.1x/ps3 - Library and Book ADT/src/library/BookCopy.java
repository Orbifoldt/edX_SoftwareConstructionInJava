package library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    // A copy of a book is a Book with a condition
    private final Book book;
    private Condition condition;
    
    // Rep invariant:
    //    book is always an instance of Book and condition is always either GOOD or DAMAGED
    // Abstraction function:
    //    Given an instance of BookCopy we have an actual book given by 'book' with a certain condition specified
    //    by 'condition'
    // Safety from rep exposure:
    //    The representation is safe from exposure since the book field is private, final and immutable and the
    //    condition is private.

    public static enum Condition {
        GOOD, DAMAGED
    }
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = book;
        this.condition = Condition.GOOD;
        this.checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert (this.condition.equals(Condition.DAMAGED) || this.condition.equals(Condition.GOOD));
    }
    
    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        String state;
        if (condition.equals(Condition.GOOD)) state = "good";
        else state = "damaged";
        return book.toString() + " (condition: " + state + ")";
    }


    // We don't need to define equals and hashCode methods since BookCopy is mutable

    // uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't
    // @Override
    // public boolean equals(Object that) {
    //     throw new RuntimeException("not implemented yet");
    // }
    // 
    // @Override
    // public int hashCode() {
    //     throw new RuntimeException("not implemented yet");
    // }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
