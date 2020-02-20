package org.rnilbong.lophorina.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static HttpResponse get(String requestURL, Map<String, String> headerMap) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(requestURL);
            for (String key : headerMap.keySet()) {
                getRequest.addHeader(key, headerMap.get(key));
            }
            response = client.execute(getRequest);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }

    public static HttpResponse post(String requestURL, String body, Map<String, String> headerMap) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(requestURL);
            for (String key : headerMap.keySet()) {
                postRequest.addHeader(key, headerMap.get(key));
            }
            postRequest.setEntity(new StringEntity(body));
            response = client.execute(postRequest);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }

    public static HttpResponse put(String requestURL, String body, Map<String, String> headerMap) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPut putRequest = new HttpPut(requestURL);
            for (String key : headerMap.keySet()) {
                putRequest.addHeader(key, headerMap.get(key));
            }
            putRequest.setEntity(new StringEntity(body));
            response = client.execute(putRequest);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }

    public static HttpResponse delete(String requestURL, Map<String, String> headerMap) throws Exception {
        HttpResponse response;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpDelete deleteRequest = new HttpDelete(requestURL);
            for (String key : headerMap.keySet()) {
                deleteRequest.addHeader(key, headerMap.get(key));
            }
            response = client.execute(deleteRequest);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }

        return response;
    }
}
