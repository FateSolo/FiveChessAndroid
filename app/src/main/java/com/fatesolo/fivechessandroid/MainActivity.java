package com.fatesolo.fivechessandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText passWord;
    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private boolean isConnect = false;
    private static final String ConnectError = "无法连接到服务器, 请检查网络";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                send("/Login " + userName.getText().toString() + " " + passWord.getText().toString());
                break;
            case R.id.register:
                send("/Register " + userName.getText().toString() + " " + passWord.getText().toString());
                break;
            case R.id.exit:
                isConnect = false;

                break;
        }
    }

    private void init() {
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
    }

    private void connect() {
        AsyncTask<Void, String, Void> conn = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    socket = new Socket("192.168.132.136", 7110);
                    reader = new DataInputStream(socket.getInputStream());
                    writer = new DataOutputStream(socket.getOutputStream());
                    isConnect = true;

                    while(isConnect) {
                        publishProgress(reader.readUTF());
                    }
                } catch (IOException e) {
                    isConnect = false;
                    Toast.makeText(MainActivity.this, ConnectError, Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    socket.close();
                    reader.close();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                receive(values[0]);
            }
        };
        conn.execute();
    }

    private void receive(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void send(String msg) {
        try {
            if(isConnect) {
                writer.writeUTF(msg);
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            isConnect = false;
            Toast.makeText(this, ConnectError, Toast.LENGTH_SHORT).show();
        }
    }
}
