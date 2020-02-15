package org.rnilbong.lophorina.sample.jyp.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static HttpResponse get(String requestURL) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(requestURL);

            response = client.execute(getRequest);

//            if (response.getStatusLine().getStatusCode() == 200) {
//                ResponseHandler<String> handler = new BasicResponseHandler();
//                String body = handler.handleResponse(response);
//                logger.debug(body);
//            } else {
//                logger.error("response is error : " + response.getStatusLine().getStatusCode());
//            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }

    public static HttpResponse post(String requestURL, String jsonMessage) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(requestURL);
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Connection", "keep-alive");
            postRequest.setHeader("Content-Type", "application/json");
            //postRequest.addHeader("Authorization", token);

            postRequest.setEntity(new StringEntity(jsonMessage));

            response = client.execute(postRequest);

//            if (response.getStatusLine().getStatusCode() == 200) {
//                ResponseHandler<String> handler = new BasicResponseHandler();
//                String body = handler.handleResponse(response);
//                logger.debug(body);
//            } else {
//                logger.error("response is error : " + response.getStatusLine().getStatusCode());
//            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }
}
