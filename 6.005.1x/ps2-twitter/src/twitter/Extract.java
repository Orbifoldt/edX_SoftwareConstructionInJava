package twitter;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.size() == 0){
            return new Timespan(Instant.MIN, Instant.MIN);
        }
        assert !tweets.isEmpty();
        Instant minTime = Instant.MAX;
        Instant maxTime = Instant.MIN;
        for (Tweet tweet : tweets) {
            Instant time = tweet.getTimestamp();
            if (maxTime.compareTo(time) < 0) {
                maxTime = time;
            }
            if (minTime.compareTo(time) > 0) {
                minTime = time;
            }
        }
        return new Timespan(minTime,maxTime);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.  //NOTE BY JEROEN: They can't start with a number or "-", but allowed here
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentions = new HashSet<>();
        Pattern mentionRegex = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\\\]))[@]([A-Za-z0-9_-]+)"); // actually  ...[@]([A-Za-z_-][A-Za-z0-9_-]{0,19})");  since no longer than 20 chars,     //(?<=X)	X, via zero-width positive lookbehind
        for (Tweet tweet : tweets) {
            Matcher match = mentionRegex.matcher(tweet.getText());
            while (match.find()) {
//                String mention = match.group(0);
                // remove the @ symbol
                mentions.add(match.group().replaceFirst("^[^A-Za-z0-9_-]+", ""));
//                mentions.add(mention);
            }
        }
        return mentions;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
