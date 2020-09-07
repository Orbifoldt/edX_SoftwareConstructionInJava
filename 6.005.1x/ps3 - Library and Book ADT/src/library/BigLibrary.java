package library;

import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    // rep
    private final Map<Book, Set<BookCopy>> allBooks;
    private final Map<Book, Set<BookCopy>> inLibrary;
    private final Map<Book, Set<BookCopy>> checkedOut;

    // rep invariant:
    //      All bookcopies in allBooks are the same as the union of all bookcopies in inLibrary and checkedOut
    //      Also the sets all bookcopies in inLibrary and in checkedOut are disjunct
    //
    // abstraction function:
    //    represents the collection of books allBooks, with possible copies of the same book as values of this map,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // safety from rep exposure argument:
    //    the collections are private and rep is checked in all creators, modifiers etc


    public BigLibrary() {
        this.allBooks = new HashMap<>();
        this.inLibrary = new HashMap<>();
        this.checkedOut = new HashMap<>();
        this.checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        // Make sets of all bookcopies in the different maps
        Set<BookCopy> allBookCopies = new HashSet<>();
        Set<BookCopy> bookCopiesInLibrary = new HashSet<>();
        Set<BookCopy> bookCopiesCheckedOut = new HashSet<>();
        for (Set<BookCopy> bookSet : allBooks.values()) allBookCopies.addAll(bookSet);
        for (Set<BookCopy> bookSet : inLibrary.values()) bookCopiesInLibrary.addAll(bookSet);
        for (Set<BookCopy> bookSet : checkedOut.values()) bookCopiesCheckedOut.addAll(bookSet);

        // Check that union of inLibrary and checkedOut equals allBooks
        Set<BookCopy> union = new HashSet<>(bookCopiesInLibrary);
        union.addAll(bookCopiesCheckedOut);
        assert union.equals(allBookCopies);

        // Check that intersection of inLibrary and checkedOut is empty
        Set<BookCopy> intersection = new HashSet<>(bookCopiesInLibrary);
        intersection.retainAll(bookCopiesCheckedOut);
        assert intersection.isEmpty();
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy newCopy = new BookCopy(book);
        // if the book is alreayd in the collection add this copy to the lists of all copies
        if (allBooks.containsKey(book)) {
            allBooks.get(book).add(newCopy);
            inLibrary.get(book).add(newCopy);
        }
        // if it's not already in the collection add this book as key to all maps and create an one-element set for
        // the maps allBooks and inLibrary (containing this new copy) and an empty set for the checkedOut map
        else {
            allBooks.put(book, new HashSet<>(Arrays.asList(newCopy)));
            inLibrary.put(book, new HashSet<>(Arrays.asList(newCopy)));
            checkedOut.put(book, new HashSet<>());
        }
        checkRep();
        return newCopy;
    }

    // assumes copy is available
    @Override
    public void checkout(BookCopy copy) {
        inLibrary.get(copy.getBook()).remove(copy);
        checkedOut.get(copy.getBook()).add(copy);
        checkRep();
    }

    // assumes copy is available
    @Override
    public void checkin(BookCopy copy) {
        checkedOut.get(copy.getBook()).remove(copy);
        inLibrary.get(copy.getBook()).add(copy);
        checkRep();
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        if (allBooks.containsKey(book)) return new HashSet<>(allBooks.get(book));
        else return new HashSet<BookCopy>();
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        if (inLibrary.containsKey(book)) return new HashSet<>(inLibrary.get(book));
        else return new HashSet<BookCopy>();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        if (!allBooks.containsKey(copy.getBook())) {
            return false;
        }
        return inLibrary.get(copy.getBook()).contains(copy);
    }


    /**
     * implements {@link Library#find find} method of the Library interface, but it has the following extra options
     * and sortings:<P>
     *  - if the query contains a phrase in quotation marks then it will count as a single search term, i.e. it will
     *      search for books whose title or authors contain exact matches of the term. Using this will disable
     *      boolean search inside this<P>
     *  - if no copies of the book of a certain match are available it will appear at the bottom of the list. This
     *      requires that query has no more than 10000 matches per book. All non-available matches will still appear
     *      in the same order as specified above for available books <P>
     *  - BEFORE and AFTER modifiers: by adding "BEFORE XXXX" or "AFTER XXXX" to the phrase, where XXXX is a 1 to 4
     *      digit number, one can look for books publicized strictly between certain years
     *  - The results are ordered in a nicer way, i.e. first to author, then to title and then to year (for more detail
     *      see return part below)
     * @param query search string which may contain boolean search terms
     * @return an ordered set of the search results, first ordered according to number of matches, then to authors,
     *          (if comparable, i.e. if same number of authors, note that order matters), then to title and then to
     *          year. This is according to the custom defined ordering in Book-class
     *
     * <P><P>
     * ====FUTURE EXPANSION IDEAS=====
     *  - The words inside the query now act as keywords, i.e. we only search for exact matches of that word with
     *      whitespaces on either side of that word. To find partial matches use an asterix (see Boolean search) <P>
     *  - Boolean search:<br>
     *      *   AND:    For a query of the form "a AND b" this will search for titles or authors that contain both "a"
     *                  and "b" (these a and b can themselves again be new queries inside parentheses )<br>
     *      *   OR:     For a query of the form "a OR b" this will search for titles or authors that contain either "a"
     *                  or "b" (these a and b can themselves again be new queries inside parentheses )
     *                  (this is how a white-space character is automatically interpreted)<br>
     *      *   NOT:    For a query containing "a NOT b" this will search for titles or authors that contain "a" but
     *                  don't contain "b" (these a and b can themselves again be new queries inside parentheses )<br>
     *      *   ( and ) (parentheses):
     *                  sjf <br>
     *      *   " and " (quotation marks):
     *                  See above. Extra feature: boolean search keywords inside parentheses are not recognized <br>
     *      *   BEFORE: This searches for books with year before a given year. This should always be followed by an
     *                  integer, else it uses Integer.MAX_VALUE (doubles and longs get automatically converted to int)
     *                  If the value is negative it returns an empty list. Only recognizes the first occurence of
     *                  BEFORE in the query. <br>
     *      *   AFTER:  This searches for books with year after a given year. This should always be followed by an
     *                  integer, else it uses 0 (doubles and longs get automatically converted to int)<br>. Only
     *                  recognizes the first occurence of AFTER in the query.
     *      *   * (wildcard) with whitespaces on either sides:
     *                  Only works inside quotation marks and acts as a wildcard,
     *                  e.g. query = "a "b * c"" searches for titles and authors that contain a or contain
     *                  phrases that start with b and end with c<br>
     *      *   * (wildcard) with letter or number on either side:
     *                  Works anywhere in the query <P>
     *
     */
    @Override
    public List<Book> find(String query) {
        // First search for quotation marks and remove them from query and put them in a separate list
        List<String> exactMatches = removeQuotes(query);
        query = exactMatches.get(0);
        exactMatches.remove(0);

        // Search for "BEFORE XXXX" and remove it from query and put the number XXXX in a separate variable
        String[] removeBeforeAndGetYear = removeBefore(query);
        query = removeBeforeAndGetYear[0];
        int yearBefore = Integer.parseInt(removeBeforeAndGetYear[1]);

        // Search for "AFTER XXXX" and remove it from query and put the number XXXX in a separate variable
        String[] removeAfterAndGetYear = removeAfter(query);
        query = removeAfterAndGetYear[0];
        int yearAfter = Integer.parseInt(removeAfterAndGetYear[1]);

        // split remaining query into words
        ArrayList<String> words =  new ArrayList<String>(Arrays.asList(query.split("\\s+")));
        // add everything what we found in between quotation marks
        words.addAll(exactMatches);


        // To automatically sort the results according to number of matches we make a tree-map with as keys the number
        // of matches which are sorted in reverse, i.e. in decreasing number of matches
        Map<Double, SortedSet<Book>> results = new TreeMap<>(Collections.reverseOrder());
        // now go through all the books in the collection
        for (Book book : allBooks.keySet()) {
            // Check if the book is in the required period
            if (book.getYear() >= yearBefore || book.getYear() <= yearAfter) continue;

            // we count the number of matches per book, which will determine its ranking
            double ranking = 0;
            for (String word : words) {
                // check if the title matches the word
                if (book.getTitle().contains(word)) ranking++;
                // check if any of the authors matches the word
                for (String author : book.getAuthors()){
                    if (author.contains(word)) ranking++;
                }
            }

            // if no copies of this book are available, we decrease its ranking to appear at the bottom of the list
            if (inLibrary.get(book).isEmpty()) ranking *= 0.0001;

            // add exactly one book corresponding to a matching copy to the result list
            if (results.containsKey(ranking)) {
                results.get(ranking).add(book);
            } else {
                results.put(ranking, new TreeSet<>(Arrays.asList(book)));
            }
        }

        // Now we make a list with all books with >0 matches. Since the order of results is in decreasing size of the
        // keys this will automatically sort the results according to the number of matches
        List<Book> resultsBooks = new ArrayList<>();
        for (Double key : results.keySet()) if (key > 0) {
            resultsBooks.addAll(results.get(key));
        }
        return resultsBooks;
    }

    /**
     * Given a string this removes all phrases that appear in between quotation marks an replaces them
     * with whitespaces. Only finds phrases of length >0
     * @param query the string from which we remove the phrases
     * @return a list with all substrings that appeared between quotation marks, but with the quotation marks removed
     */
    private List<String> removeQuotes(String query) {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(query);
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");  // a quotation mark, >0 non-quotation-mark characters
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String match = matcher.group(0);
            quotes.add(match.substring(1,match.length()-1));
            query = query.replace(match, " ");
            // query was modified, update matcher
            matcher = pattern.matcher(query);
        }
        return quotes;
    }

    /**
     * Given a string, this removes the first phrase of the form "BEFORE XXXX" where XXXX is a 1 to 4 digits long
     * number which represents a year.
     * @param query the string from which we remove the phrase
     * @return an array in which the first element is the query with the "BEFORE XXXX"-part removed and the second
     *          element is the string version of "XXXX" in which XXXX is a 1 to 4 digit number. If there is no match
     *          found then this string is "9999".
     */
    private String[] removeBefore(String query) {
        String yearBefore = "9999";
        Pattern pattern = Pattern.compile("([B][E][F][O][R][E])(\\s+)(\\d{1,4})");  // the word BEFORE, some whitespace and 1 to 4 digits
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String match = matcher.group(0);
            // Extract the year
            Matcher digits = Pattern.compile("\\d{1,4}").matcher(match);
            digits.find();
            yearBefore = digits.group(0);
            query = query.replace(match, " ");
        }
        String [] result = new String[2];
        result[0] = query;
        result[1] = yearBefore;
        return result;
    }

    /**
     * Given a string, this removes the first phrase of the form "AFTER XXXX" where XXXX is a 1 to 4 digits long
     * number which represents a year.
     * @param query the string from which we remove the phrase
     * @return an array in which the first element is the query with the "AFTER XXXX"-part removed and the second
     *          element is the string version of "XXXX" in which XXXX is a 1 to 4 digit number. If there is no match
     *          found this string is "0".
     */
    private String[] removeAfter(String query) {
        String yearAfter = "0";
        Pattern pattern = Pattern.compile("([A][F][T][E][R])(\\s+)(\\d{1,4})");  // the word AFTER, some whitespace and 1 to 4 digits
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String match = matcher.group(0);
            // Extract the year
            Matcher digits = Pattern.compile("\\d{1,4}").matcher(match);
            digits.find();
            yearAfter = digits.group(0);
            query = query.replace(match, " ");
        }
        String [] result = new String[2];
        result[0] = query;
        result[1] = yearAfter;
        return result;
    }



//    /**
//     * Given a string this removes all phrases that appear in between parentheses an replaces them
//     * with whitespace. Only finds phrases of length > 0.
//     * @param query the string from which we remove the phrases
//     * @return a list with all substrings that appeared between parentheses, but with the quotation marks removed
//     */
//    private List<String> removeSubstringsInParentheses(String query) {
//        ArrayList<String> quotes = new ArrayList<>();
//        Pattern pattern = Pattern.compile("[(]([^)]+?)[)]");  // an opening parenthesis, >0 non-parenthesis-characters (greedy) and a closing parenthesis
//        Matcher matcher = pattern.matcher(query);
//        while (matcher.find()) {
//            String match = matcher.group(0);
//            quotes.add(match.substring(1,match.length()-1));
//            query = query.replace(match, " ");
////           query was modified, update matcher
//            matcher = pattern.matcher(query);
//        }
//        return quotes;
//    }

//    /**
//     * Given a string query, this finds all the books whose titles or authors give an exact match with the query
//     * This passes exactly once through all books
//     * @param query the search string
//     * @return all books with an exact match
//     */
//    private TreeMap<Integer, SortedSet<Book>> exactMatchFind(String query) {
//        // To automatically sort the results according to number of matches we make a tree-map with as keys the number
//        // of matches which are sorted in reverse, i.e. in decreasing number of matches
//        Map<Integer, SortedSet<Book>> results = new TreeMap<>(Collections.reverseOrder());
//        // now go through all the books in the collection
//        for (Book book : allBooks.keySet()) {
//            // we count the number of matches per book
//            int numberMatches = 0;
//            // check if the title matches the word
//            if (book.getTitle().contains(query)) numberMatches++;
//            // check if any of the authors matches the word
//            for (String author : book.getAuthors()) {
//                if (author.contains(query)) numberMatches++;
//            }
//            // add exactly one book corresponding to a matching copy to the result list
//            if (results.containsKey(numberMatches)) {
//                results.get(numberMatches).add(book);
//            } else {
//                results.put(numberMatches, new TreeSet<>(Arrays.asList(book)));
//            }
//        }
//        return new TreeMap<>(results);
//    }



    /**
     * Fast version of find which does not incorporate the boolean search (since this requires multiple passes through
     * all the books) This is the old search method, similar to the one used in smallLibrary, but may contain some
     * older mistakes, so use at OWN RISK!
      * @param query
     * @return
     */
    public List<Book> fastFind(String query) {
        // split query into words
        String[] words = query.split("\\s+");

        // To automatically sort the results according to number of matches we make a tree-map with as keys the number
        // of matches which are sorted in reverse, i.e. in decreasing number of matches
        Map<Double, SortedSet<Book>> results = new TreeMap<>(Collections.reverseOrder());
        // now go through all the books in the collection
        for (Book book : allBooks.keySet()) {

            // we count the number of matches per book, which will determine its ranking
            double ranking = 0;
            for (String word : words) {
                // check if the title matches the word
                if (book.getTitle().contains(word)) ranking++;
                // check if any of the authors matches the word
                for (String author : book.getAuthors()){
                    if (author.contains(word)) ranking++;
                }
            }

            // if no copies of this book are available, we decrease its ranking to appear at the bottom of the list
            if (inLibrary.get(book).isEmpty()) ranking *= 0.0001;

            // add exactly one book corresponding to a matching copy to the result list
            if (results.containsKey(ranking)) {
                results.get(ranking).add(book);
            } else {
                results.put(ranking, new TreeSet<>(Arrays.asList(book)));
            }
        }

        // Now we make a list with all books with >0 matches. Since the order of results is in decreasing size of the
        // keys this will automatically sort the results according to the number of matches
        List<Book> resultsBooks = new ArrayList<>();
        for (Double key : results.keySet()) if (key > 0) {
            resultsBooks.addAll(results.get(key));
        }
        return resultsBooks;
    }




    // asserts that the book corresponding to copy is a key in allBooks
    @Override
    public void lose(BookCopy copy) {
        allBooks.get(copy.getBook()).remove(copy);
        inLibrary.get(copy.getBook()).remove(copy);
        checkedOut.get(copy.getBook()).remove(copy);
        if (allBooks.get(copy.getBook()).isEmpty()){
            allBooks.remove(copy.getBook());
            inLibrary.remove(copy.getBook());
            checkedOut.remove(copy.getBook());
        }
        checkRep();
    }

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
