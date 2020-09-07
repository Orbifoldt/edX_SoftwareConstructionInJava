package library;

import java.util.*;

/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;
    
    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // safety from rep exposure argument:
    //    the collections are private and rep is checked in all creators, modifiers etc
    
    public SmallLibrary() {
        this.inLibrary = new HashSet<>();
        this.checkedOut = new HashSet<>();
        this.checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        Set<BookCopy> intersection = new HashSet<>(inLibrary); // use the copy constructor
        intersection.retainAll(checkedOut);
        assert intersection.isEmpty();
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy newBookCopy = new BookCopy(book);
        inLibrary.add(newBookCopy);
        this.checkRep();
        return newBookCopy;
    }

    // asserts copy is available
    @Override
    public void checkout(BookCopy copy) {
        inLibrary.remove(copy);
        checkedOut.add(copy);
        this.checkRep();
    }

    // asserts copy is lent
    @Override
    public void checkin(BookCopy copy) {
        checkedOut.remove(copy);
        inLibrary.add(copy);
        this.checkRep();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        if (inLibrary.contains(copy)) return true;
        return false;
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> allCopiesOfBook = new HashSet<>();
        for (BookCopy bookCopy : inLibrary) {
            if (book.equals(bookCopy.getBook())) {
                allCopiesOfBook.add(bookCopy);
            }
        }
        for (BookCopy bookCopy : checkedOut) {
            if (book.equals(bookCopy.getBook())) {
                allCopiesOfBook.add(bookCopy);
            }
        }
        return allCopiesOfBook;
    }
    
    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> availableCopiesOfBook = new HashSet<>();
        for (BookCopy bookCopy : inLibrary) {
            if (book.equals(bookCopy.getBook())) {
                availableCopiesOfBook.add(bookCopy);
            }
        }
        return availableCopiesOfBook;
    }

    @Override
    public List<Book> find(String query) {
        // split query into words
        String[] words = query.split("\\s+");
//        for (int i = 0; i < words.length; i++) {
//            // remove all punctuation and symbols except periods
//            words[i] = words[i].replaceAll("[^\\w.]", "");
//        }
        // To automatically sort the results according to number of matches we make a tree-map with as keys the number
        // of matches which are sorted in reverse, i.e. in decreasing number of matches
        Map<Integer, ArrayList<Book>> results = new TreeMap<>(Collections.reverseOrder());
        // now go through all the books in the collection
        Set<BookCopy> allBooks = new HashSet<>(inLibrary);
        allBooks.addAll(checkedOut);
        for (BookCopy bookCopy : allBooks) {
            // we count the number of matches per book
            int numberMatches = 0;
            // go through all the words of the query
            for (String word : words) {
                // check if the title matches the word
                if (bookCopy.getBook().getTitle().contains(word)) {
                    numberMatches++;
                }
                // check if any of the authors matches the word
                for (String author : bookCopy.getBook().getAuthors()){
                    if (author.contains(word)) {
                        numberMatches++;
                    }
                }
            }
            // add exactly one book corresponding to a matching copy to the result list
            if (results.containsKey(numberMatches)) {
                if (!results.get(numberMatches).contains(bookCopy.getBook())){
                    results.get(numberMatches).add(bookCopy.getBook());
                }
            } else {
                ArrayList<Book> bookCopyList = new ArrayList<>();
                results.put(numberMatches, bookCopyList);
                results.get(numberMatches).add(bookCopy.getBook());
            }
        }
        List<Book> resultsBooks = new ArrayList<>();
        for (Integer key : results.keySet()) if (key > 0) {
            // First sort the books with the same number of matches according to the years, then add to results
            Collections.sort(results.get(key));//, (book1, book2) -> ((Integer)book2.getYear()).compareTo((book1.getYear())));
            resultsBooks.addAll(results.get(key));
        }
        return resultsBooks;
    }

    // asserts that copy is either available or lent (i.e. this copy once was bought,
    //                                                  so its in inLibrary or in checkedOut)
    @Override
    public void lose(BookCopy copy) {
        if (isAvailable(copy)) inLibrary.remove(copy);
        else checkedOut.remove(copy);
        checkRep();
    }

    // No need for this, since library is mutable
//    // uncomment the following methods if you need to implement equals and hashCode,
//    // or delete them if you don't
//    // @Override
//    // public boolean equals(Object that) {
//    //     throw new RuntimeException("not implemented yet");
//    // }
//    //
//    // @Override
//    // public int hashCode() {
//    //     throw new RuntimeException("not implemented yet");
//    // }
    

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
