package com.fatesolo.fivechessandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText passWord;

    private boolean isQuit = false;

    private SingletonSocket singletonSocket;

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
        try {
            switch (v.getId()) {
                case R.id.login:
                    singletonSocket.sendMsg("/Login " + userName.getText().toString() + " " + passWord.getText().toString());
                    break;
                case R.id.register:
                    singletonSocket.sendMsg("/Register " + userName.getText().toString() + " " + passWord.getText().toString());
                    break;
                case R.id.exit:
                    singletonSocket.disconnect();
                    isQuit = true;
                    break;
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "连接失败, 请重试", Toast.LENGTH_SHORT).show();
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
                singletonSocket = SingletonSocket.getInstance();

                try {
                    singletonSocket.connect();

                    while (!isQuit) {
                        publishProgress(singletonSocket.recvMsg());
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "连接失败, 请重试", Toast.LENGTH_SHORT).show();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();
            }
        };
        conn.execute();
    }
}
