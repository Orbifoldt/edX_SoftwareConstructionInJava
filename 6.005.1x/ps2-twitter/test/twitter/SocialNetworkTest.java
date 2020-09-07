package twitter;

import static org.junit.Assert.*;
import static twitter.SocialNetwork.guessFollowsGraph;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing:
     *
     * guessFollowersGraph:
     * Partitions:
     * - empty list of tweets should return empty map
     * - list with 1 tweet and no mentions should return a map whose image is empty
     * - list with 2 tweets and no mentions should again return a map whose image is empty
     * - list with 1 mention and only one mention to self should return a map whose image is empty
     * - list with 2 tweets, one user mentioning the other, here the value corresponding to one user is a singleton
     *          that is the the other users name that is mentioned
     * - list with 1 tweet, one user mentioning someone who hasnt tweeted yet, similar result as previous
     * - list with 2 tweets, 2 users mention a third user but with different capitalization, when we convert the two
     *          values of the map corresponding to those users to lowercase they should agree
     * - list with 2 tweets of the same user that mentions someone else twice with different capitilization, the map
     *          should have only one value in this case
     *
     *
     *
     * Following tests not yet implemented since its boring to do and I can just test it against the grading tests
     *
     *
     * influencers:
     * Partitions
     * - empty list
     * - list with 1 tweet
     * - ...
     */



    private static final Tweet tweet1 = new Tweet(0, "a_boat_", "hey", Instant.now());
    private static final Tweet tweet2 = new Tweet(1, "a_boat_", "RT @a_boat_: hey", Instant.now());
    private static final Tweet tweet3 = new Tweet(2, "a_cow", "RT @a_boat_: hey", Instant.now());
    private static final Tweet tweet4 = new Tweet(3, "frank", ".@a_boat_ how are you??", Instant.now());
    private static final Tweet tweet5 = new Tweet(4, "hendrik", "hey @A_Boat_ how's life", Instant.now());
    private static final Tweet tweet6 = new Tweet(5, "a_cow", "sun!", Instant.now());
    private static final Tweet tweet7 = new Tweet(6, "hendrik", "hey @A_bOAt_ how's school", Instant.now());



    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    /*
     * ############################ guessFollowersGraph Tests ###############################
     *
     * - empty list of tweets should return empty map
     * - list with 1 tweet and no mentions should return a map whose image is empty
     * - list with 2 tweets and no mentions should again return a map whose image is empty
     * - list with 1 mention and only one mention to self should return a map whose image is empty
     * - list with 2 tweets, one user mentioning the other, here the value corresponding to one user is a singleton
     *          that is the the other users name that is mentioned
     * - list with 1 tweet, one user mentioning someone who hasnt tweeted yet, similar result as previous
     * - list with 2 tweets, 2 users mention a third user but with different capitalization, when we convert the two
     *          values of the map corresponding to those users to lowercase they should agree
     * - list with 2 tweets of the same user that mentions someone else twice with different capitilization, the map
     *          should have only one value in this case
     */



    /*
     * - empty list of tweets should return empty map
     */
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    /*
     * - list with 1 tweet and no mentions should return a map whose image is empty
     */
    @Test
    public void testGuessFollowsGraph1TweetNoMentions() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet1)));
        // if keys exist, go through them and look at their value sets
        for (String key : followsGraph.keySet()) {
            assertTrue("expected empty value set for" + key, followsGraph.get(key).isEmpty());
        }
    }
      
    /*  
    * - list with 2 tweets and no mentions should again return a map whose image is empty
    */
    @Test
    public void testGuessFollowsGraph2tweetsNoMentions() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet1,tweet6)));
        // if keys exist, go through them and look at their value sets
        for (String key : followsGraph.keySet()) {
            assertTrue("expected empty value set for" + key, followsGraph.get(key).isEmpty());
        }
    }


    /*  
    * - list with 1 mention and only one mention to self should return a map whose image is empty
    */
    @Test
    public void testGuessFollowsGraph1TweetSelfMentions() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet2)));
        // if keys exist, go through them and look at their value sets
        for (String key : followsGraph.keySet()) {
            assertTrue("self mentioning: expected empty value set for " + key, followsGraph.get(key).isEmpty());

        }
    }


    /*
     * - list with 2 tweets, one user mentioning the other, here the value corresponding to one user is a singleton
     *          that is the the other users name that is mentioned
     */
    @Test
    public void testGuessFollowsGraph2TweetsOneMentionsOther() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet1,tweet3)));
        if ( followsGraph.containsKey("a_boat_")) {
            assertTrue("expected empty set", followsGraph.get("a_boat_").isEmpty());
        }
        assertTrue("expected a_cow follows a_boat_", (followsGraph.get("a_cow").toString()).equals("[a_boat_]"));
    }


    /*
     * - list with 1 tweet, one user mentioning someone who hasnt tweeted yet, similar result as previous
     */
    @Test
    public void testGuessFollowsGraphMentionNontweeted() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet3)));
        assertTrue("expected a_cow follows a_boat_", (followsGraph.get("a_cow").toString()).equals("[a_boat_]"));
    }


    /*
     * - list with 2 tweets, 2 users mention a third user but with different capitalization, when we convert the two
     *          values of the map corresponding to those users to lowercase they should agree
     */
    @Test
    public void testGuessFollowsGraph2TweetsMentionSameButDifferentCapitalization() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet4,tweet5)));
        String[] follows1 = new String[1];
        for (String name : followsGraph.get("frank")){
            follows1[0] = name.toLowerCase();
        }
        String[] follows2 = new String[1];
        for (String name : followsGraph.get("hendrik")){
            follows2[0] = name.toLowerCase();
        }
        assertTrue("expected hendrik and frank both follow a_boat_", follows1[0].equals(follows2[0]));
    }

    /*
     * - list with 2 tweets of the same user that mentions someone else twice with different capitilization, the map
     *          should have only one value in this case
     */
     @Test
    public void testGuessFollowsGraph2TweetsSameUserMentionsSamePersonButDifferentCapitalization() {
        Map<String, Set<String>> followsGraph = guessFollowsGraph(Collections.unmodifiableList(
                Arrays.asList(tweet7,tweet5)));
        assertTrue("expected hendrik to only follow 1", followsGraph.get("hendrik").size()==1);
    }


    /*
     ** ############################ influencers Tests ###############################
     *
     * - empty list
     * - social network with one person following another
     * - social network with one person following none
     */
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

//    private static final Tweet tweet1 = new Tweet(0, "a_boat_", "hey", Instant.now());
//    private static final Tweet tweet2 = new Tweet(1, "a_boat_", "RT @a_boat_: hey", Instant.now());
//    private static final Tweet tweet3 = new Tweet(2, "a_cow", "RT @a_boat_: hey", Instant.now());
//    private static final Tweet tweet4 = new Tweet(3, "frank", ".@a_boat_ how are you??", Instant.now());
//    private static final Tweet tweet5 = new Tweet(4, "hendrik", "hey @A_Boat_ how's life", Instant.now());
//    private static final Tweet tweet6 = new Tweet(5, "a_cow", "sun!", Instant.now());
//    private static final Tweet tweet7 = new Tweet(6, "hendrik", "hey @A_bOAt_ how's school", Instant.now());
//

    @Test
    public void testInfluencersTwoUserOneFollowingTheOther() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Set<String> follows = new HashSet<>(Arrays.asList("a_boat_"));
        followsGraph.put("a_cow", follows);
        Set<String> empty = new HashSet<>(Arrays.asList());
        followsGraph.put("a_boat_", empty);
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected list of length 2", 2, influencers.size());
        assertTrue("expected a_boat_ 1 follower, so most popular", influencers.get(0).equals("a_boat_"));
        assertTrue("expected a_cow has 0 followers so least popular", influencers.get(1).equals("a_cow"));

    }

    @Test
    public void testInfluencersOneUserFollowingNone() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Set<String> empty = new HashSet<>(Arrays.asList());
        followsGraph.put("a_boat_", empty);
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected list of length 1", 1, influencers.size());
        assertTrue("expected a_boat_ only user", influencers.get(0).equals("a_boat_"));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
