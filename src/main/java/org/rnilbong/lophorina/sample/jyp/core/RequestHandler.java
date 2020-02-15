package org.rnilbong.lophorina.sample.jyp.core;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.rnilbong.lophorina.sample.jyp.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class RequestHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_FILE_PATH = "images/Lophorina.jpg";

    private static final String DEFAULT_DOMAIN = "http://localhost";
    private static final String DEFAULT_PORT = "8080";

    private static final String PORT_DELIMITER = ":";

    private Socket connectionSocket;

    public RequestHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("WebServer Thread Created");
        BufferedReader inFromClient;
        BufferedWriter outToClient;

        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//            outToClient = new DataOutputStream(connectionSocket.getOutputStream()); //image
            outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())); //http

            //head
            String requestMessageLine = inFromClient.readLine();
            if (requestMessageLine == null) {
                return;
            }
            logger.debug("head: " + requestMessageLine);

            //request header
            String headerLine;
            while ((headerLine = inFromClient.readLine()).length() != 0) {
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
            String requestUrl = "";

            HttpResponse httpResponse = null;
            if (requestMethod.equals("GET")) {
                requestUrl = tokenizedLine.nextToken();

                httpResponse = HttpUtil.get(getPassUrl() + requestUrl);
                logger.debug(httpResponse.toString());
            } else if (requestMethod.equals("POST")) {
                requestUrl = tokenizedLine.nextToken();

                httpResponse = HttpUtil.post(getPassUrl() + requestUrl, payload.toString());
                logger.debug(httpResponse.toString());
            } else {
                outToClient.write("HTTP/1.1 400 Bad Request Message \r\n");
                outToClient.write("Connection: close\r\n");
                outToClient.write("\r\n");
                logger.error("Bad Request");
            }

            if (httpResponse != null) {
                sendResponse(httpResponse, outToClient);
            }
//            sendResponseDefaultImage(requestUrl, outToClient);

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
    
    private void sendResponse(HttpResponse httpResponse, BufferedWriter outToClient) throws IOException {
        StringBuilder outputHeader = new StringBuilder();
        outputHeader.append("HTTP/1.1 200 OK")
                .append("\r\n");
        for (Header header : httpResponse.getAllHeaders()) {
            outputHeader.append(header.toString())
                    .append("\r\n");
        }

        ResponseHandler<String> handler = new BasicResponseHandler();
        String body = handler.handleResponse(httpResponse);

        logger.debug(outputHeader + "\r\n\r\n" + body);
        outToClient.write(outputHeader + "\r\n\r\n");
        outToClient.write(body, 0, body.length());
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
