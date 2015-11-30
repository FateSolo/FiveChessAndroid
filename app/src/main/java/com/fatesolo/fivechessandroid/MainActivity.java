package com.fatesolo.fivechessandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText passWord;

    private FiveChessService.FiveChessBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (FiveChessService.FiveChessBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
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
                    binder.getService().sendMsg("/Login " + userName.getText().toString() + " " + passWord.getText().toString());
                    break;
                case R.id.register:
                    binder.getService().sendMsg("/Register " + userName.getText().toString() + " " + passWord.getText().toString());
                    break;
                case R.id.exit:

                    break;
            }
        } catch (IOException e) {
            EventBus.getDefault().post("/ConnectError");
        }
    }

    private void init() {
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

        Intent intent = new Intent(this, FiveChessService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public void onEventMainThread(String msg) {
        String[] list = msg.split(" ");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        switch (list[0]) {
            case "/ConnectError":
                Toast.makeText(MainActivity.this, "网络连接错误, 请检查您的网络", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
