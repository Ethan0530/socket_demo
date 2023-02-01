package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


//服务器
public class MyServer {
    //保存所有客户端连接，用于广播
    static HashSet<Socket> clientSockets = new HashSet<>();

    //创建线程池
    static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            10,
            30,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(20),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    //广播方法(线程中调用)
    public static void broadcast(String msg) throws IOException {
        PrintWriter out = null;
        for(Socket clientSocket:clientSockets){
            if(clientSocket.isOutputShutdown()){
                continue;
            }
            out = new PrintWriter(clientSocket.getOutputStream());
            out.write(msg+"\n");
            out.write("@#$\n");//写入结束标记
            out.flush();
        }
        System.out.println("测试："+msg);
    }

    public static void init(){
        //GUI面板
        JFrame frame = new JFrame("服务器");
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);
        JLabel jLabel = new JLabel("port:");
        jLabel.setBounds(20,20,50,30);
        panel.add(jLabel);
        JTextField textField = new JTextField(20);
        textField.setBounds(80,20,200,30);
        panel.add(textField);


        JButton jButton = new JButton("start");
        jButton.setBounds(300,20,80,30);
        panel.add(jButton);

        frame.setContentPane(panel);
        frame.setVisible(true);

        //定义事件内部类
        class MyActionListener implements ActionListener{
            JTextField textField;

            public MyActionListener(JTextField textField) {
                this.textField = textField;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(textField.getText());
                System.out.println("服务器已在"+port+"端口启动");
                try{
                    //服务器在指定端口监听
                    ServerSocket serverSocket = new ServerSocket(port);
                    //循环接受客户端发来的socket连接请求
                    while(true){
                        //接受客户端连接并保存至集合
                        Socket clientSocket = serverSocket.accept();
                        clientSockets.add(clientSocket);
                        System.out.println("客户端"+clientSocket.getRemoteSocketAddress().toString()+"已连接");
                        //为客户端创建线程，用于接收客户端发送的消息
                        Thread thread = new Thread(new ThreadSocket(clientSocket));
                        //放入线程池
                        pool.submit(thread);
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }
        //为按钮添加点击事件
        jButton.addActionListener(new MyActionListener(textField));
    }

    public static void main(String[] args) {
        init();
    }
}
