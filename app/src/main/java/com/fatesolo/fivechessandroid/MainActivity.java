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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String username = userName.getText().toString();
            String password = passWord.getText().toString();

            switch (v.getId()) {
                case R.id.login:
                    if (!stringTest(4, 10, username)) {
                        Toast.makeText(MainActivity.this, "用户名无效, 请输入长度在4-10位之间的字母与数字组合", Toast.LENGTH_SHORT).show();
                    } else if (!stringTest(6, 10, password)) {
                        Toast.makeText(MainActivity.this, "密码无效, 请输入长度在6-10位之间的字母与数字组合 ", Toast.LENGTH_SHORT).show();
                    } else {
                        binder.getService().sendMsg("/Login " + username + " " + password);
                    }
                    break;
                case R.id.register:
                    if (!stringTest(4, 10, username)) {
                        Toast.makeText(MainActivity.this, "用户名无效, 请输入长度在4-10位之间的字母与数字组合", Toast.LENGTH_SHORT).show();
                    } else if (!stringTest(6, 10, password)) {
                        Toast.makeText(MainActivity.this, "密码无效, 请输入长度在6-10位之间的字母与数字组合 ", Toast.LENGTH_SHORT).show();
                    } else {
                        binder.getService().sendMsg("/Register " + username + " " + password);
                    }
                    break;
                case R.id.exit:
                    unbindService(connection);

                    Intent intent = new Intent(this, FiveChessService.class);
                    stopService(intent);

                    finish();
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

        switch (list[0]) {
            case "/ConnectError":
                Toast.makeText(MainActivity.this, "网络连接错误, 请检查您的网络", Toast.LENGTH_SHORT).show();
                break;
            case "/UsernameNotExist":
                Toast.makeText(MainActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                break;
            case "/PasswordError":
                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                break;
            case "/UserHasLogged":
                Toast.makeText(MainActivity.this, "用户已经登陆", Toast.LENGTH_SHORT).show();
                break;
            case "/UsernameExist":
                Toast.makeText(MainActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private boolean stringTest(int min, int max, String s) {
        String pattern = String.format("^[a-z0-9A-Z]{%d,%d}$", min, max);
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(s);

        return mat.find();
    }
}
