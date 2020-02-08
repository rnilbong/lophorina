package org.rnilbong.lophorina;

import org.rnilbong.lophorina.core.SocketThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        ServerSocket listenSocket;
        try {
            listenSocket = new ServerSocket(80);
            System.out.println("WebServer Socket Created");

            Socket connectionSocket;
            SocketThread socketThread;

            // 순환을 돌면서 클라이언트의 접속을 받는다.
            // accept()는 Blocking 메서드이다.
            while ((connectionSocket = listenSocket.accept()) != null) {
                // 서버 쓰레드를 생성하여 실행한다.
                socketThread = new SocketThread(connectionSocket);
                socketThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
