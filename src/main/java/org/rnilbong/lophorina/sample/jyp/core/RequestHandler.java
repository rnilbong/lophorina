package org.rnilbong.lophorina.sample.jyp.core;

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
    private Socket connectionSocket;

    public RequestHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("WebServer Thread Created");
        BufferedReader inFromClient;
        DataOutputStream outToClient;

        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            String requestMessageLine = inFromClient.readLine();
            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

            String requestMethod = tokenizedLine.nextToken();
            String requestUrl = "";

            if (requestMethod.equals("GET")) {
                requestUrl = tokenizedLine.nextToken();
                HttpUtil.get("http://localhost" + ":8080" + requestUrl + "v1/sample");
            } else if(requestMethod.equals("POST")) {
                requestUrl = tokenizedLine.nextToken();
                HttpUtil.post("http://localhost" + ":8080" + requestUrl + "v1/sample", "[\n" +
                        "  \"1\",\n" +
                        "  \"2\",\n" +
                        "  \"3\"\n" +
                        "]");
            } else {
                outToClient.writeBytes("HTTP/1.0 400 Bad Request Message \r\n");
                outToClient.writeBytes("Connection: close\r\n");
                outToClient.writeBytes("\r\n");
                logger.error("Bad Request");
            }

            getMainImage(requestUrl, outToClient);

            connectionSocket.close();
            logger.debug("Connection Closed");
        } catch (IOException ioe) {
            logger.error(ioe.getLocalizedMessage());
            ioe.printStackTrace();
        }
    }

    private void getMainImage(String requestUrl, DataOutputStream outToClient) throws IOException {
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
