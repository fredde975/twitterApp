package com.serverless;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(Handler.class);
    private static final String CONSUMER_KEY = "SSVbNkmx7ugzaOfaSsG8iRSij";
    private static final String CONSUMER_SECRET = "fBop27mqeaK8tcYp5hmL7vmSWSfbp3ys4qP14JgWyqMsh5UA6X";
    private static final String ACCESS_TOKEN = "899157129722048512-YHceWk6N45aBm4g4rSrQH3EXxwPjaPG";
    private static final String ACCESS_TOKEN_SECRET = "hNI81vC3XbSp3tpiQ4m1dywL1KbJ0W3O0LqfcDwCsMAqO";
    private static final int TWEETS_PER_QUERY = 100;
    private static final int MAX_QUERIES = 5;


    private static Map<String, WordItem> resultMap = new HashMap<>();


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);
        Response responseBody = new Response("Go Serverless v1.x! Your function executed successfully!", input);

        final String hashTag;
        try {
            hashTag = "#" + readTagFromQueryString(input);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
            return ApiGatewayResponse.builder()
                    .setStatusCode(422)
                    .setObjectBody(e.getMessage())
                    .build();
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            checkLimits(twitter);
        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
            return ApiGatewayResponse.builder()
                    .setStatusCode(511)
                    .setObjectBody("Twitter rate limit exceded")
                    .build();
        }

        Query queryMax = new Query(hashTag);
        queryMax.setCount(TWEETS_PER_QUERY);
        try {
            getTweets(queryMax, twitter);
        } catch (TwitterException e) {
            LOG.error(e.getMessage());
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(e.getMessage())
                    .build();
        }

        List<WordItem> wordItems = resultMap.values().stream()
                .sorted()
                .limit(100)
                .collect(Collectors.toList());

        wordItems.stream().forEach(LOG::info);


        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(wordItems)
                .build();
    }


    private String readTagFromQueryString(Map<String, Object> input) throws IllegalArgumentException {
        String tag;
        HashMap queryStringParameters = (HashMap) input.get("queryStringParameters");

        if (queryStringParameters != null) {
            tag = (String) queryStringParameters.get("twitterTag");
            if (tag != null && tag.length() > 0) {
                return tag;
            }
        }

        throw new IllegalArgumentException("You must have the query string 'twitterTag=tagname' set in the url");
    }


    private void checkLimits(Twitter twitter) {
        Map<String, RateLimitStatus> rateLimitStatus = null;
        try {
            rateLimitStatus = twitter.getRateLimitStatus("search");
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

            String message = String.format("You have %d calls remaining out of %d, Limit resets in %d seconds\n",
                    searchTweetsRateLimit.getRemaining(),
                    searchTweetsRateLimit.getLimit(),
                    searchTweetsRateLimit.getSecondsUntilReset());

            LOG.info(message);

            if (searchTweetsRateLimit.getRemaining() == 0) {
                throw new IllegalStateException(message);
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private void getTweets(Query query, Twitter twitter) throws TwitterException {
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
    }

    private void handleTweetText(Status status) {
        String text = status.getText();
        List<String> ord = Arrays.asList(text.split("\\s+"));
        ord.stream().forEach(word -> addWordToMap(word));
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
