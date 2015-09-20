package com.fatesolo.fivechessandroid;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SingletonSocket {

    private Socket socket = null;
    private InputStreamReader reader = null;
    private OutputStreamWriter writer = null;

    private boolean isConnect = false;

    private static SingletonSocket instance = null;

    public static SingletonSocket getInstance() {
        if (instance == null) {
            instance = new SingletonSocket();
        }
        return instance;
    }

    private SingletonSocket() {
    }

    public void connect() throws IOException {
        try {
            socket = new Socket("192.168.132.136", 7110);
            reader = new InputStreamReader(socket.getInputStream());
            writer = new OutputStreamWriter(socket.getOutputStream());

            isConnect = true;
        } catch (IOException e) {
            disconnect();
            throw new IOException();
        }
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }

        isConnect = false;
    }

    public void sendMsg(String msg) throws IOException {
        try {
            if (!isConnect) {
                connect();
            }

            writer.write(msg);
            writer.flush();
        } catch (IOException e) {
            disconnect();
            throw new IOException();
        }
    }

    public String recvMsg() throws IOException {
        try {
            char[] data = new char[1024];
            int length = reader.read(data);

            return new String(data, 0, length);
        } catch (IOException e) {
            disconnect();
            throw new IOException();
        }
    }
}
