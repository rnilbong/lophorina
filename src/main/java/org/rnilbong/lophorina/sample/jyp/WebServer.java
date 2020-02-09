package org.rnilbong.lophorina.sample.jyp;

import org.rnilbong.lophorina.sample.jyp.core.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public static void main(String[] args) {
        ServerSocket listenSocket;
        try {
            listenSocket = new ServerSocket(80);
            System.out.println("WebServer Socket Created");

            Socket connectionSocket;
            RequestHandler requestHandler;

            // 순환을 돌면서 클라이언트의 접속을 받는다.
            // accept()는 Blocking 메서드이다.
            while ((connectionSocket = listenSocket.accept()) != null) {
                // 서버 쓰레드를 생성하여 실행한다.
                requestHandler = new RequestHandler(connectionSocket);
                requestHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
