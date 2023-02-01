package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.example.MyServer.broadcast;

//保持与客户端连接的线程
//接受对应客户端发来的信息，并将消息广播给所有客户端
public class ThreadSocket implements Runnable{
    private Socket clientSocket;

    public ThreadSocket() {
    }

    public ThreadSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg = clientSocket.getInetAddress().toString() + ":";
                String line;
                while ((line = br.readLine()) != null) {
//                    System.out.println("line="+line+"END");//测试
                    if(line.trim().equals("@#$")) break;
                    msg += line;
                }
                broadcast(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
