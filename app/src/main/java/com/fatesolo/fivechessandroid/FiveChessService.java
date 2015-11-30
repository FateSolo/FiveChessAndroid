package com.fatesolo.fivechessandroid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import de.greenrobot.event.EventBus;

public class FiveChessService extends Service {

    private Socket socket = null;
    private InputStreamReader reader = null;
    private OutputStreamWriter writer = null;

    private boolean isConnect = false;

    private FiveChessBinder binder = new FiveChessBinder();

    public FiveChessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            disconnect();
        } catch (IOException e) {
            EventBus.getDefault().post("/ConnectError");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

//    public boolean isConnect() {
//        return isConnect;
//    }

    public void connect() {
        isConnect = true;

        new Thread(new FiveChessThread()).start();
    }

    public void disconnect() throws IOException {
        isConnect = false;

        if (socket != null) {
            socket.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
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

    class FiveChessBinder extends Binder {

        public FiveChessService getService() {
            return FiveChessService.this;
        }

    }

    class FiveChessThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket("192.168.132.144", 7110);
                reader = new InputStreamReader(socket.getInputStream());
                writer = new OutputStreamWriter(socket.getOutputStream());

                while (isConnect) {
                    String msg = recvMsg();
                    int length = msg.length();

                    for(int tmp, curr = 0; curr != length; curr += tmp) {
                        tmp = Integer.parseInt(msg.substring(curr, curr + 4));
                        curr += 4;

                        EventBus.getDefault().post(msg.substring(curr, curr + tmp));
                    }
                }
            } catch (IOException e) {
                EventBus.getDefault().post("/ConnectError");
            }
        }

    }

}
