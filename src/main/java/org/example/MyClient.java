package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


//客户端
public class MyClient extends JFrame{
    private static JTextArea textArea2 = new JTextArea();

    public static void init(){
        final JFrame jFrame = new JFrame("客户端");
        jFrame.setSize(300,250);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);

        JLabel label1 = new JLabel("address:");
        JLabel label2 = new JLabel("port:");
        label1.setBounds(20,20,100,30);
        label2.setBounds(20,60,100,30);
        panel.add(label1);
        panel.add(label2);
        final JTextField textField1 = new JTextField(20);
        final JTextField textField2 = new JTextField(20);
        textField1.setBounds(120,20,150,30);
        textField2.setBounds(120,60,150,30);
        panel.add(textField1);
        panel.add(textField2);
        JButton button1 = new JButton("连接");
        button1.setBounds(40,120,200,30);
        panel.add(button1);

        jFrame.setContentPane(panel);
        jFrame.setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.setVisible(false);
                try {
                    createClient(textField1.getText().trim(),Integer.parseInt(textField2.getText().trim()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    public static void createClient(final String address, final int port) throws IOException {
        final Socket socket = new Socket(address, port);
        socket.setSoLinger(true,50000);
        JFrame jFrame = new JFrame("客户端");
        jFrame.setSize(600, 400);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(null);

        JLabel label1 = new JLabel("address: " + socket.getRemoteSocketAddress().toString());
        JLabel label2 = new JLabel("port: " + socket.getPort());
        label1.setBounds(20, 20, 200, 30);
        label2.setBounds(20, 60, 200, 30);
        panel.add(label1);
        panel.add(label2);
        JButton button1 = new JButton("清空");
        JButton button2 = new JButton("发送");
        button1.setBounds(30, 300, 100, 30);
        button2.setBounds(160, 300, 100, 30);
        panel.add(button1);
        panel.add(button2);

        final JTextArea textArea1 = new JTextArea();
        textArea1.setBounds(20, 180, 250, 90);
        panel.add(textArea1);

        textArea2.setBounds(300, 30, 250, 300);
        textArea2.setEditable(false);
        panel.add(textArea2);

        jFrame.setContentPane(panel);
        jFrame.setVisible(true);


        //为客户端创建一个线程，用于接收服务端广播,并显示在textArea2上
        Thread thread = new Thread(new BroadcastListener(textArea2));
        thread.start();

        //清零按钮
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = textArea1.getText().trim();
                if (s != "") {
                    textArea1.replaceRange("", 0, s.length());
                }
            }
        });

        class MyActionListenner implements ActionListener {
            private String address;
            private Integer port;
            private JTextArea textArea;


            public MyActionListenner(String address, Integer port, JTextArea textArea) {
                this.address = address;
                this.port = port;
                this.textArea = textArea;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textArea1.getText().trim();
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(socket.getOutputStream());
                    pw.write(msg+"\n");
                    pw.write("@#$\n");//自定义结束符,可以在不管流的情况下判断对方输入是否结束
                    pw.flush();
//                    socket.shutdownOutput();
                    textArea.setText("");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        //通信按钮
        button2.addActionListener(new MyActionListenner(address,port,textArea1));
    }


    public static void main(String[] args) throws IOException {
        init();
    }
}
