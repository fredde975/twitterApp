package com.serverless;

import java.util.*;

import com.serverless.exceptions.RateExceededException;
import com.serverless.exceptions.TagInputException;
import com.serverless.models.ApiGatewayResponse;
import com.serverless.models.WordItem;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import twitter4j.*;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = Logger.getLogger(Handler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        TwitterService twitterService = new TwitterService();
        LOG.info("received: " + input);

        final String hashTag;
        try {
            hashTag = createHashtagFromQueryString(input);
            List<WordItem> wordItems = twitterService.handleRequest(hashTag);

            return getApiGatewayResponse(wordItems);
        } catch (TagInputException e) {
            return getApiGatewayExceptionResponse(e, 422);  //not correct input
        } catch (TwitterException e) {
            return getApiGatewayExceptionResponse(e, 500);  //any Twitter exception
        } catch (RateExceededException e) {
            return getApiGatewayExceptionResponse(e, 509);  //read limit reached
        }
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


    private String createHashtagFromQueryString(Map<String, Object> input) throws TagInputException {
        String tag;
        HashMap queryStringParameters = (HashMap) input.get("queryStringParameters");

        if (queryStringParameters != null) {
            tag = (String) queryStringParameters.get("twitterTag");
            if (tag != null && tag.length() > 0 && !tag.startsWith("#")) {
                LOG.info("Recieved tag: " + tag);
                return "#" + tag;
            }
        }

        throw new TagInputException("You must have the query string 'twitterTag=tagname' set in the url, i.e. don't use '#' in the query");
    }

}
