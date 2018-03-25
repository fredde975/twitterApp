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

    //Map<String, WordItem> resultMap = new HashMap<>();
    TwitterService twitterService = new TwitterService();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);

        final String hashTag;
        try {
            hashTag = createHashtagFromQueryString(input);

            Twitter twitter = createTwitterInstance();
            TwitterUtil.checkLimits(twitter);
            Query queryMax = createQuery(hashTag);
            Map<String, WordItem> resultMap = twitterService.getTweets(queryMax, twitter);
            List<WordItem> wordItems = createSortedList(resultMap);

            return getApiGatewayResponse(wordItems);

        } catch (IllegalArgumentException e) {
            return getApiGatewayExceptionResponse(e, 422);  //not correct input
        } catch (IllegalStateException e) {
            return getApiGatewayExceptionResponse(e, 511);  //read limit reached
        } catch (TwitterException e) {
            return getApiGatewayExceptionResponse(e, 500);  //any Twitter exception
        }
    }



    private Query createQuery(String hashTag) {
        Query queryMax = new Query(hashTag);
        queryMax.setCount(TWEETS_PER_QUERY);
        return queryMax;
    }


    private Twitter createTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }


    private ApiGatewayResponse getApiGatewayResponse(List<WordItem> wordItems) {
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(wordItems)
                .build();
    }


    private ApiGatewayResponse getApiGatewayExceptionResponse(Exception e, int responseCode) {
        LOG.error(e.getMessage());

        return ApiGatewayResponse.builder()
                .setStatusCode(responseCode)
                .setObjectBody(e.getMessage())
                .build();
    }


    private String createHashtagFromQueryString(Map<String, Object> input) throws IllegalArgumentException {
        String tag;
        HashMap queryStringParameters = (HashMap) input.get("queryStringParameters");

        if (queryStringParameters != null) {
            tag = (String) queryStringParameters.get("twitterTag");
            if (tag != null && tag.length() > 0 && !tag.startsWith("#")) {
                LOG.info("Recieved tag: " + tag);
                return "#" + tag;
            }
        }

        throw new IllegalArgumentException("You must have the query string 'twitterTag=tagname' set in the url, i.e. don't use '#' in the query");
    }


    private List<WordItem> createSortedList(Map<String, WordItem> resultMap) {
        List<WordItem> wordItems = resultMap.values().stream()
                .sorted()
                .limit(100)
                .collect(Collectors.toList());

        wordItems.stream().forEach(LOG::info);
        return wordItems;
    }

}
