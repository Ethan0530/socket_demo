package org.example;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//用来监听服务器广播的线程
public class BroadcastListener implements Runnable {
    private JTextArea jTextArea;

    public BroadcastListener() {
    }

    public BroadcastListener(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1",10000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true){
                    String msg = "";
                    String line;
                    while ((line = br.readLine())!=null){
                        if(line.trim().equals("@#$")) break;
                        msg += line;
                    }
                    jTextArea.append(msg+"\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
