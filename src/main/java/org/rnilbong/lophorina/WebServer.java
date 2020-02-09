package org.rnilbong.lophorina;

import org.rnilbong.lophorina.core.RequestHandler;

public class WebServer {
    public static void main(String[] args) {
        RequestHandler requestHandler = new RequestHandler();
        requestHandler.run();
    }
}
