package com.fatesolo.fivechessandroid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import de.greenrobot.event.EventBus;

public class FiveChessService extends Service {

    private Socket socket = null;
    private InputStreamReader reader = null;
    private OutputStreamWriter writer = null;

    private static boolean isConnect = false;

    private UserInformation user = null;

    private FiveChessBinder binder = new FiveChessBinder();

    public FiveChessService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connect(intent.getStringExtra("type"), intent.getStringExtra("username"), intent.getStringExtra("password"));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static boolean isConnect() {
        return isConnect;
    }

    public void connect(String type, String username, String password) {
        isConnect = true;

        new Thread(new FiveChessThread(type + username + " " + password)).start();
    }

    public void disconnect() {
        isConnect = false;

        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            writer.write(msg);
            writer.flush();
        } catch (Exception e) {
            EventBus.getDefault().post("/ConnectError");

            disconnect();
        }
    }

    public String recvMsg() throws Exception {
        char[] data = new char[1024];
        int length = reader.read(data);

        return new String(data, 0, length);
    }

    public UserInformation getUser() {
        return user;
    }

    public void setUser(UserInformation user) {
        this.user = user;
    }

    class FiveChessBinder extends Binder {

        public FiveChessService getService() {
            return FiveChessService.this;
        }

    }

    class FiveChessThread implements Runnable {

        private String instruction;

        public FiveChessThread(String instruction) {
            this.instruction = instruction;
        }

        @Override
        public void run() {
            try {
                socket = new Socket();

                SocketAddress socketAddress = new InetSocketAddress("192.168.132.147", 7110);
                socket.connect(socketAddress, 3000);

                reader = new InputStreamReader(socket.getInputStream());
                writer = new OutputStreamWriter(socket.getOutputStream());

                sendMsg(instruction);

                while (isConnect) {
                    String msg = recvMsg();
                    int length = msg.length();

                    for (int tmp, curr = 0; curr != length; curr += tmp) {
                        tmp = Integer.parseInt(msg.substring(curr, curr + 4));
                        curr += 4;

                        EventBus.getDefault().post(msg.substring(curr, curr + tmp));
                    }
                }
            } catch (Exception e) {
                EventBus.getDefault().post("/ConnectError");

                disconnect();
            }
        }

    }

}
