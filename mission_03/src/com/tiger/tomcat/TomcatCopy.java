package com.tiger.tomcat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * web服务器
 */
public class TomcatCopy {
    public static void main(String[] args) throws Exception {
        System.out.println("服务器启动，等待连接。。。。");
        //创建服务器对象
        ServerSocket server = new ServerSocket(8888);
        //循环等待
        while (true){
            //创建socket对象
            Socket socket = server.accept();
            //启动线程，多次请求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = socket.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line = br.readLine();
                        String[] arr = line.split(" ");
                        String path = arr[1].substring(1);
                        System.out.println("路径："+path);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(path);
                        } catch (FileNotFoundException e) {
                            
                            StringBuilder response = new StringBuilder();
                            response.append("HTTP/1.1 404 File Not Found\r\n");
                            response.append("Content-Type: text/html\r\n");
                            response.append("Content-Length: 23\r\n");
                            response.append("\r\n");
                            response.append("<h1>File Not Found</h1>");
                            OutputStream os = socket.getOutputStream();
                            os.write(response.toString().getBytes());
                            return;
                        }
                        OutputStream os = socket.getOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        //向浏览器 回写数据
                        os.write("HTTP/1.1 200 OK\r\n".getBytes());
                        os.write("Content-Type:text/html\r\n".getBytes());
                        os.write("\r\n".getBytes());
                        while((len = fis.read(bytes))!=-1){
                            os.write(bytes, 0, len);
                        }
                        os.close();
                        fis.close();
                        br.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
