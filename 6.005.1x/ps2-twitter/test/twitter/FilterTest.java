package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Tests, not yet implemented since its boring to do and I can just test it against the grading tests
     *
     *
     * writtenBy
     *
     * Partitions:
     * - empty list of tweets
     * - list of tweets, one of which is written by username
     * - list of tweets but name in different capitalization
     * - list of tweets with name, check that order is preserved
     * - list of tweets without mention of the name
     *
     *
     * inTimespan
     *
     * Partitions:
     * - empty list of tweets
     * - same end as start with 1 tweet outside timespan
     * - non-zero timespan with multiples tweets in timespan, check order
     * - non-zero timespan with only tweets outside timespan
     *
     *
     * Containing
     *
     * Partitions:
     * - empty word list should return all tweets
     * - list with one word and some tweets that contain it and some that dont (one and one suffices)
     * - list with multiple words and two tweets that contain each a different and no more than only one word
     *          of the list
     * - list of words and tweet that doesnt contain any of those
     * - list of one word and tweet that contains many times that same word, it should only appear once
     *
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet4 = new Tweet(4, "alyssa", "talk talk talk talk appel", d2);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }



    /*
     * ############################ writtenBy Tests ###############################
     * - empty list of tweets
     * - list of tweets, one of which is written by username
     * - list of tweets but name in different capitalization
     * - list of tweets with name, check that order is preserved
     * - list of tweets without mention of the name
     */

    @Test
    public void testWrittenByEmpty() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");

        assertEquals("expected empty list", 0, writtenBy.size());
    }

    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");

        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByDifferentCapitalization() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alYSSa");

        assertFalse("expected non-empty result", writtenBy.isEmpty());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByOrderPreserved() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1,tweet3), "alyssa");

        assertTrue("expected first element to be tweet 1", writtenBy.get(0).equals(tweet1));
        assertTrue("expected second element to be tweet 2", writtenBy.get(1).equals(tweet3));
    }

    @Test
    public void testWrittenByNoMention() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1,tweet3), "fred");

        assertEquals("expected empty list", 0, writtenBy.size());
    }





    /*
     * ############################ inTimespan Tests ###############################
     * - empty list of tweets
     * - same end as start with 1 tweet outside timespan
     * - non-zero timespan with multiples tweets in timespan, check order
     * - non-zero timespan with only tweets outside timespan
     */

    @Test
    public void testInTimespanEmpty() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        assertTrue("expected empty list", inTimespan.isEmpty());
      }

    @Test
    public void testInTimespanZeroLengthTimeSpan() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertTrue("expected empty list", inTimespan.isEmpty());
    }


    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testInTimespanMultipleTweetsOutsideTimespan() {
        Instant testStart = Instant.parse("2015-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2015-02-17T12:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertTrue("expected non-empty list", inTimespan.isEmpty());
    }



    /*
     * ############################ containing Tests ###############################
     * - empty word list should return all tweets
     * - list with one word and some tweets that contain it and some that dont (one and one suffices)
     * - list with multiple words and two tweets that contain each a different and no more than only one word
     *          of the list
     * - list of words and tweet that doesnt contain any of those
     * - list of one word and tweet that contains many times that same word, it should only appear once
     */

    @Test
    public void testContainingEmpty() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList());

        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingDifferentWordsDifferentTweets() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet2,tweet4), Arrays.asList("talk","appel"));

        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet2,tweet4)));
        assertEquals("expected same order", 0, containing.indexOf(tweet2));
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet4), Arrays.asList("groen", "paars","blauw"));

        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingManyTimesSameWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet4), Arrays.asList("talk"));

        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet4)));
        assertEquals("expected only one copy of the tweet", 1, containing.size());
    }



    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
