package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class MyExtractTest {

    /*
     * Testing strategy:
     *
     *
     * GetTimespan:
     *
     * Partition:
     * - empty list: should give Timespan of length 0, any starting time is allowed
     * - only one tweet, should give timespan of length 0 starting at given timestamp of the one tweet
     * - two tweets with same timestamp (representative for any set with same timestamps), should give again timespan of
     *                            length 0
     * - three tweets (with newest tweet at index 1)
     *
     *
     * GetMentionedUsers:
     *
     * Partition:
     * - empty list of tweets: should give empty list of mentions
     * - one tweet with no mentions, should give same
     * - one tweet with one mention mid-tweet with leestekens (?,.,etc)
     * - one tweet with two different mentions, mid-tweet and at start
     * - one tweet with email adress
     * - one tweet with two duplicate mentions
     * - multiple tweets with multiple mentions
     * - one tweet with mention of length 1
     * - one tweet with mention of length 21 (should only recognize first 20 characters
     * - one tweet with mention starting with digit should not be recognized
     * - one tweet with mention starting with - should not be recognized
     * - one tweet with just an @ sign
     * - one tweet with ".@username" mention to test . works inbefore @
     * - one tweet with "@_username", starting with _ should be recognized
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2017-05-19T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(2, "hank", "#nosleep", d1);
    private static final Tweet tweet4 = new Tweet(2, "fred", "who am i?", d2);
    private static final Tweet tweet5 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testGetTimespanEmptyListZeroTime() {
        /*
         * The timespan of an empty list should be zero length. We don't care
         * what time is put in there.
         */
        Timespan timespan = Extract.getTimespan(new ArrayList<Tweet>());
        if (timespan.getStart() == timespan.getEnd()) {
            return;
        }
        if (!timespan.getStart().equals(timespan.getEnd())) {
            fail();
        }
    }

    @Test
    public void testGetTimespanOneTweet() {
        /*
         * The timespan of a list containing a single tweet should also have
         * zero length equal to the timestamp of the tweet.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
    }

    @Test
    public void testGetTimespanTwoSameTime() {
        /*
         * This test is identical to the case for a single tweet. We want the
         * resultant timespan to be the minimal covering timespan.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3));
        assertEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
    }


    @Test
    public void testGetTimespanMultipleTweetsWithDuplicates() {
        /*
         * Testing to make sure duplicates are handled as well as more than two
         * tweets as input.
         */

        // Testing duplicates at the start of the timespan.
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3,
                tweet2));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet2.getTimestamp(), timespan.getEnd());

        // Testing duplicates at the end of the timespan.
        timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet4));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet2.getTimestamp(), timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleTimesWithoutDuplicates() {
        /*
         * Testing to make sure more than two inputs are handled.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2,
                tweet5));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet5.getTimestamp(), timespan.getEnd());

        // Check order doesnt matter
        timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet5, tweet1));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet5.getTimestamp(), timespan.getEnd());
    }

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }


    /*
    * Test getMentionedUsers
    */

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersNoTweets() {
        List<Tweet> emptyList = new ArrayList<>();
        Set<String> mentionedUsers = Extract.getMentionedUsers(emptyList);

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersOneMentionWithLeestekens() {
        Tweet tweet = new Tweet(1, "someone", "is it reasonable to talk about rivest so much @henk?", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention = "@henk";
        assertTrue("expected @henk", mentionedUsers.contains(mention));
    }

    @Test
    public void testGetMentionedUsersTwoDifferentMentions() {
        Tweet tweet = new Tweet(1, "someone", "is it reasonable @fr33 to talk about rivest so much @henk", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention1 = "@henk";
        String mention2 = "@fr33";
        assertTrue("expected @henk", mentionedUsers.contains(mention1));
        assertTrue("expected @fr33", mentionedUsers.contains(mention2));
    }


    @Test
    public void testGetMentionedUsersTwoDifferentMentionsOneAtStart() {
        Tweet tweet = new Tweet(1, "someone", "@fr33 to talk about rivest so much @henk", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention1 = "@henk";
        String mention2 = "@fr33";
        assertTrue("expected @henk", mentionedUsers.contains(mention1));
        assertTrue("expected @fr33", mentionedUsers.contains(mention2));
    }

    @Test
    public void testGetMentionedUsersEmail() {
        Tweet tweet = new Tweet(1, "someone", "Hoi gsakfj@fr33.com to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }


    @Test
    public void testGetMentionedUsersTwoDuplicateMentions() {
        Tweet tweeta = new Tweet(1, "someone", "sjfkl @henk to talk about rivest so much @henk", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweeta));
        String mention1 = "@henk";
        System.out.println(mentionedUsers+"here");
        assertTrue("expected @henk", mentionedUsers.contains(mention1));
        assertTrue("expected contains only one element", mentionedUsers.size()==1);
    }

    @Test
    public void testGetMentionedUsersMultipleTweetsAndShortName() {
        Tweet tweetje1 = new Tweet(1, "someone", "@abcdeabcde to talk about rivest so much", d1);
        Tweet tweetje2 = new Tweet(2, "someone", "@fr33c to talk about @a rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetje1,tweetje2));
        String mention1 = "@abcdeabcde";
        String mention2 = "@fr33c";
        String mention3 = "@a";
        assertTrue("expected @abcdeabcde", mentionedUsers.contains(mention1));
        assertTrue("expected @fr33c", mentionedUsers.contains(mention2));
        assertTrue("expected @a", mentionedUsers.contains(mention3));
    }

    @Test
    public void testGetMentionedUsersNotNumber() {
        Tweet tweetje1 = new Tweet(1, "someone", "@abcdeabcdeabcdeabcdefgh to talk about @3am rivest so much", d1);
        Tweet tweetje2 = new Tweet(2, "someone", "@fr33c to talk about @a rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetje1,tweetje2));
        String nonMention = "@3am";
        assertFalse("expected not @3am", mentionedUsers.contains(nonMention));
    }


    @Test
    public void testGetMentionedUsersOnlyAtSymbol() {
        Tweet tweet = new Tweet(1, "someone", "sjfkl @ to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String nonMention = "@";
        assertFalse("expected not @", mentionedUsers.contains(nonMention));
    }



    /*
    * The following are extra tests! Not required by the pre- and post-conditions
     */
    @Test
    public void testGetMentionedUserAfterDot() {
        Tweet tweet = new Tweet(1, "someone", ".@henk sjfkl to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention = "@henk";
        assertTrue("expected @henk", mentionedUsers.contains(mention));
    }

    @Test
    public void testGetMentionedUsersStartWithDash() {
        Tweet tweet = new Tweet(1, "someone", "hoi @-henk sjfkl to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention = "@-henk";
        assertFalse("expected not @-henk", mentionedUsers.contains(mention));
    }

    @Test
    public void testGetMentionedUsersStartWithUnderscore() {
        Tweet tweet = new Tweet(1, "someone", "hoi @_henk sjfkl to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
        String mention = "@_henk";
        assertTrue("expected @_henk", mentionedUsers.contains(mention));
    }

    @Test
    public void testGetMentionedUsersLongName() {
        Tweet tweetje1 = new Tweet(1, "someone", "sfjklj @abcdeabcdeabcdeabcdefgh to talk about rivest so much", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetje1));
        String mention1 = "@abcdeabcdeabcdeabcde";
        assertTrue("expected @abcdeabcdeabcdeabcde, not fgh at the end", mentionedUsers.contains(mention1));
    }

 /* Partition:



      * - one tweet with just an @ sign
     * - one tweet with ".@username" mention to test . works inbefore @
     */

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     *
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
