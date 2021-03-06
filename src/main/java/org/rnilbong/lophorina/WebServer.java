package org.rnilbong.lophorina;

import org.rnilbong.lophorina.core.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public static void main(String[] args) {
        ServerSocket listenSocket;
        try {
            listenSocket = new ServerSocket(80);
            logger.debug("WebServer Socket Created");

            Socket connectionSocket;

            while ((connectionSocket = listenSocket.accept()) != null) {
                RequestHandler requestHandler = new RequestHandler(connectionSocket);
                requestHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
