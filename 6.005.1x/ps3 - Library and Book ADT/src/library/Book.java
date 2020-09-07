package library;

import java.util.ArrayList;
import java.util.List;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object,
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 */
public class Book implements Comparable<Book> {

    // Rep
    private final String title;
    private final List<String> authors;
    private final int year;

    // Rep invariant:
    //    - title is always a string containing at least one non-space character.
    //    - authors is always a list of strings which must have at least one string, and each string must contain
    //      at least one non-space character.
    //    - year must always be an integer > 1;
    // Abstraction function:
    //    Given above fields (title, authors and year) we can associate to this a real book in the obvious way, i.e.
    //      the real book has as title 'title', as authors the elements of 'authors' and as publication year 'year'.
    // Rep exposure:
    //    This class is safe from rep exposure since the fields are all chosen final and private. The title and year
    //      are themselves immutable. The authors list is not, however it is never directly returned, only copies
    // TODO: safety from rep exposure argument

    /**
     * Make a Book.
     *
     * @param title   Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain
     *                at least one non-space character.
     * @param year    Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative.
     */
    public Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.year = year;
        this.checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        // number of non-space characters in title must be larger than 0
        assert title.trim().length() > 0;
        // Authors must contain at least one name and the number of non-space characters
        // in all authors must be larger than 0
        assert !authors.isEmpty();
        for (String author : authors){
            assert author.trim().length() > 0;
        }
        assert year > 0;
    }

    /**
     * @return the title of this book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        return new ArrayList<String>(this.authors);
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        return year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     * authors, and publication year
     */
    public String toString() {
        String names = "";
        for (String author : authors) names += author + ", ";
        names = names.substring(0,names.length()-2);
        return String.format("'%s' (%d), written by: %s", title, year, names);
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Book)) return false;
        Book thatBook = (Book) that;
        if (!this.title.equals(thatBook.getTitle())) return false;
        if (!this.authors.equals(thatBook.getAuthors())) return false;
        if (!(this.year == thatBook.getYear())) return false;
        return true;
    }

    @Override
    public int compareTo(Book that){
        // first check if this equals that
        if (this.equals(that)) return 0;
        // then check size of the Authors-list
        if (this.authors.size() == that.getAuthors().size()) {
            // compare each author pairwise, if some pair isn't equal sort alphabetically
            for (int i = 0; i<this.authors.size(); i++) {
                if (!(this.authors.get(i) == that.getAuthors().get(i))) {
                    return this.authors.get(i).compareTo(that.getAuthors().get(i));
                }
            }
            // if all authors are the same check if titles are the same and sort those alphabetically
            if (!this.title.equals(that.getTitle())) {
                return this.title.compareTo(that.getTitle());
            }
            // else also titles are the same, now sort on year in descending order
            else {
                return ((Integer) that.getYear()).compareTo(this.year);
            }
        }
        // sort on number of authors
        else return ((Integer)this.authors.size()).compareTo(that.getAuthors().size());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 37 * result + title.hashCode();
        for (String author : authors) {
            result = 37 * result + author.hashCode();
        }
        result = 37 * result + year;
        return result;
    }






    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
