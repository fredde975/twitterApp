package com.serverless;

import com.serverless.models.WordItem;
import org.apache.log4j.Logger;
import twitter4j.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwitterRepository {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(TwitterService.class);
    private static final int MAX_QUERIES = 5;
    private Map<String, WordItem> resultMap = new HashMap<>();


    public Map<String, WordItem> getTweets(Query query, Twitter twitter) throws TwitterException {
        long maxID = -1;
        int totalTweets = 0;

        //	This is the loop that retrieve multiple blocks of tweets from Twitter
        for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {

            //does not handle rate limits

            //	If maxID is -1, then this is our first call and we do not want to tell Twitter what the maximum
            //	tweet id is we want to retrieve.  But if it is not -1, then it represents the lowest tweet ID
            //	we've seen, so we want to start at it-1 (if we start at maxID, we would see the lowest tweet
            //	a second time...
            if (maxID != -1) {
                query.setMaxId(maxID - 1);
            }

            //	This actually does the search on Twitter and makes the call across the network
            QueryResult r = twitter.search(query);

            //	If there are NO tweets in the result set, it is Twitter's way of telling us that there are no
            //	more tweets to be retrieved.  Remember that Twitter's search index only contains about a week's
            //	worth of tweets, and uncommon search terms can run out of week before they run out of tweets
            if (r.getTweets().size() == 0) {
                break;            // Nothing? We must be done
            }


            //	loop through all the tweets and process them.  In this sample program, we just print them
            //	out, but in a real application you might save them to a database, a CSV file, do some
            //	analysis on them, whatever...
            for (Status s : r.getTweets())                // Loop through all the tweets...
            {
                //	Increment our TWEETS_PER_QUERY of tweets retrieved
                totalTweets++;

                //	Keep track of the lowest tweet ID.  If you do not do this, you cannot retrieve multiple
                //	blocks of tweets...
                if (maxID == -1 || s.getId() < maxID) {
                    maxID = s.getId();
                }

                handleTweetText(s);
            }
        }


        LOG.info(String.format("\n\nA total of %d tweets retrieved\n", totalTweets));
        return resultMap;
    }

    private void handleTweetText(Status status) {
        String text = status.getText();
        Arrays.asList(text.split("\\s+"))       //split text on white space
                .stream()
                .forEach(word -> addWordToMap(word));
    }


    private void addWordToMap(String word) {
        if (resultMap.containsKey(word)) {
            WordItem item = resultMap.get(word);
            resultMap.replace(word, new WordItem(word, item.getCount() + 1));
        } else {
            resultMap.put(word, new WordItem(word, 1));
        }
    }

}
