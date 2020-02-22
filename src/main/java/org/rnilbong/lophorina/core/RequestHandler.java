package org.rnilbong.lophorina.core;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.rnilbong.lophorina.core.response.HttpResponseSender;
import org.rnilbong.lophorina.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * todo
 * 1. target server api 에 다양한 api 추가
 *     1. 여러가지 응답 확인
 *         1. 현재 json 타입이 클라이언트로 전달 안됨
 *     1. 여러가지 메서드 확인
 *         1. 현재 post, get 확인
 * 1. 404 등 에러 처리
 * 1. 코어기능 추가
 * 1. 코어 코드 리펙토링
 * 1. 테스트 코드
 */

public class RequestHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_FILE_PATH = "images/Lophorina.jpg";

    private static final String DEFAULT_DOMAIN = "http://localhost";
    private static final String DEFAULT_PORT = "8080";

    private static final String PORT_DELIMITER = ":";

    private Gson gson;
    private Socket connectionSocket;

    public RequestHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        logger.debug("WebServer Thread Created");
        BufferedReader inFromClient;
        DataOutputStream outToClient;
//        BufferedWriter outToClient;
        Map<String, String> headerMap = new HashMap<>();

        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//            outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

            //head
            String requestMessageLine = inFromClient.readLine();
            if (requestMessageLine == null) {
                return;
            }
            logger.debug("head: " + requestMessageLine);
//            String[] headSplitStr = requestMessageLine.split(": ");
//            headerMap.put(headSplitStr[0], headSplitStr[1]);

            //request header
            String headerLine;
            while ((headerLine = inFromClient.readLine()).length() != 0) {
                String[] headerSplitStr = headerLine.split(": ");
                headerMap.put(headerSplitStr[0], headerSplitStr[1]);
                logger.debug("request header: " + headerLine);
            }

            //request body
            StringBuilder payload = new StringBuilder();
            while (inFromClient.ready()) {
                payload.append((char) inFromClient.read());
            }
            logger.debug("Payload data: " + payload.toString());

            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

            String requestMethod = tokenizedLine.nextToken();
            String requestUrl;

            HttpResponse httpResponse = null;
            if (requestMethod.equals("GET")) {
                requestUrl = tokenizedLine.nextToken();

                httpResponse = HttpUtil.get(getPassUrl() + requestUrl, headerMap);
                logger.debug(httpResponse.toString());
            } else if (requestMethod.equals("POST")) {
                requestUrl = tokenizedLine.nextToken();

                httpResponse = HttpUtil.post(getPassUrl() + requestUrl, payload.toString(), headerMap);
                logger.debug(httpResponse.toString());
            } else {
                outToClient.writeBytes("HTTP/1.1 400 Bad Request Message \r\n");
                outToClient.writeBytes("Connection: close\r\n");
                outToClient.writeBytes("\r\n");
                logger.error("Bad Request");
            }

            if (httpResponse != null) {
                new HttpResponseSender().returnResponse(httpResponse, outToClient);
            }

            connectionSocket.close();
            logger.debug("Connection Closed");
        } catch (IOException ioe) {
            logger.error("ioe :" + ioe.getLocalizedMessage());
            ioe.printStackTrace();
        } catch (Exception e) {
            logger.error("e :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private String getPassUrl() {
        return DEFAULT_DOMAIN + PORT_DELIMITER + DEFAULT_PORT;
    }


    private void sendResponseDefaultImage(String requestUrl, DataOutputStream outToClient) throws IOException {
        if (requestUrl.startsWith("/")) {
            if (requestUrl.length() > 1) {
                requestUrl = requestUrl.substring(1);
            } else {
                requestUrl = DEFAULT_FILE_PATH;
            }
        }

        File file = new File(requestUrl);
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getPath());

            int numOfBytes = (int) file.length();

            FileInputStream inFile = new FileInputStream(requestUrl);
            byte[] fileInBytes = new byte[numOfBytes];
            inFile.read(fileInBytes);
            outToClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");
            outToClient.writeBytes("Content-Type: " + mimeType + "\r\n");
            outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
            outToClient.writeBytes("\r\n");

            outToClient.write(fileInBytes, 0, numOfBytes);
        } else {
            outToClient.writeBytes("HTTP/1.0 404 Not Found \r\n");
            outToClient.writeBytes("Connection: close\r\n");
            outToClient.writeBytes("\r\n");
            logger.error("Requested File Not Found : " + requestUrl);
        }
    }
}
