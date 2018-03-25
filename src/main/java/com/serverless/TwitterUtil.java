package com.serverless;

import org.apache.log4j.Logger;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Map;

public class TwitterUtil {
    private static final Logger LOG = Logger.getLogger(TwitterUtil.class);

    public static void checkLimits(Twitter twitter) throws TwitterException {

        Map<String, RateLimitStatus> rateLimitStatus;

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

    }

}
