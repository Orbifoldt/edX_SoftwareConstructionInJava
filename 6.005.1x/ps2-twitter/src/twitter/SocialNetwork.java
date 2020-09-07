package twitter;

//import javax.rmi.CORBA.Util;
import sun.management.Util;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();//<String,HashSet<String>>();
        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            assert !author.contains("@");
            Set<String> mentions = new HashSet<>();
            for (String user : Extract.getMentionedUsers(Arrays.asList(tweet))) {
                if (!((user.toLowerCase()).equals(author))) {
                    assert !author.contains("@");
                    mentions.add(user);
                }
            }
            followsGraph.put(author, mentions);
        }
        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        // We'll create a map whith keys the users in the social network and values their followers as given
        // by guessFollowersGraph
        Map<String, Set<String>> usersFollowers = new HashMap<>();

        // for all users in the social network...
        for (String user : followsGraph.keySet()) {
            usersFollowers.put(user, new HashSet<>(Arrays.asList()));
            // for all other users followed by this user, who we'll call "mentionedUser"s
            for (String followedUser : followsGraph.get(user) ) {
                // add the user to the followers of the mentionedUser
                if (usersFollowers.containsKey(followedUser)){
                    usersFollowers.get(followedUser).add(user);
                } else {
                    usersFollowers.put(followedUser,new HashSet<>(Arrays.asList(user)));
                }
            }
        }

        // We only want the number of followers, so we construct a map of these figures
        Map<String, Integer> usersFollowersNumbers = new HashMap<>();
        for (String user : usersFollowers.keySet()) {
            usersFollowersNumbers.put(user, usersFollowers.get(user).size());
        }

        // Now let's order this map according to the values, so a key (here a user) k1 comes for another key k2 if
        // its value (i.e. the number of followers) is lower than the the value of k2. This is done in the second
        // line of code. Then from this ordering we build a new ascending list of users with the most followers.
        List<String> mostFollowers = usersFollowersNumbers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))     // Comparator.reverseOrder() <=> (v1,v2)->v2.compareTo(v1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return mostFollowers;

        // The original version of my code, a bit more readable and completely equivalent while less compact
//        List<Map.Entry<String, Integer>> orderedUsersByFollowers = new ArrayList<>(usersFollowersNumbers.entrySet());
//        Collections.sort(orderedUsersByFollowers, new Comparator<Map.Entry<String, Integer>>() {
//            @Override
//            public int compare(Map.Entry<String, Integer> userAndFollowersPair1,
//                               Map.Entry<String, Integer> userAndFollowersPair2)
//            {
//                Integer numberFollowers1 = userAndFollowersPair1.getValue();
//                Integer numberFollowers2 = userAndFollowersPair2.getValue();
//                return numberFollowers2.compareTo(numberFollowers1);
//            }
//        });
//        List<String> mostFollowers = new ArrayList<>();
//        for (Map.Entry<String, Integer> keyValue : orderedUsersByFollowers) {
//            mostFollowers.add(keyValue.getKey());
//        }
//        return mostFollowers;
    }


    public static void main(String[] args) {
        final Tweet tweet1 = new Tweet(0, "a_lyssp_", "no friends :(", Instant.now());
        final Tweet tweet2 = new Tweet(1, "a_lyssp_", "RT @a_lyssp_: no friends :(", Instant.now());
        final Tweet tweet3 = new Tweet(2, "bbitdiddle", "RT @a_lyssp_: no friends :(", Instant.now());
        final Tweet tweet4 = new Tweet(3, "evalu_", ".@a_lyssp_ how are you??", Instant.now());
        final Tweet tweet5 = new Tweet(4, "_reAsOnEr", "@evalu_ @a_lyssp_: great talk yesterday!", Instant.now());
        final Tweet tweet6 = new Tweet(5, "a_lyssp_", "@evalu_ doing great! you?", Instant.now());
        final Tweet tweet7 = new Tweet(88213, "123098", "123098123098", Instant.now());
        final Tweet tweet8 = new Tweet(79789783, "asdlkj", "asdlkjasdlkj", Instant.now());
        final Tweet tweet9 = new Tweet(9, "bbitdiddle", "@A_LYSSP_ hi hi hi", Instant.now());
        final Tweet tweet10 = new Tweet(10, "bbitdiddle", "@someoneElse lol", Instant.now());

        final List<Tweet> tweets = Collections.unmodifiableList(
                Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6, tweet7, tweet8, tweet9, tweet10));
        Map ding = guessFollowsGraph(tweets);
        System.out.println(ding);
        System.out.println(influencers(ding));
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
