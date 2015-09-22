package com.fatesolo.fivechessandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class FiveChessThread extends AsyncTask<Void, String, Void> {

    Context context;

    public FiveChessThread(Context context) {
        super();

        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        SingletonSocket singletonSocket = SingletonSocket.getInstance();

        try {
            if(!singletonSocket.isConnect()) {
                singletonSocket.connect();
            }

            while(singletonSocket.isConnect()) {
                publishProgress(singletonSocket.recvMsg());
            }

        } catch (Exception e) {
            publishProgress("连接失败, 请重试");
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
    }
}
