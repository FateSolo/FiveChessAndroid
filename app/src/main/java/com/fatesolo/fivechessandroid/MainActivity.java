package com.fatesolo.fivechessandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText userName;
    private EditText passWord;

    private AlertDialog isExit;

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
    public void onClick(View v) {
        Intent intent = new Intent(this, FiveChessService.class);

        String username = userName.getText().toString();
        String password = passWord.getText().toString();

        switch (v.getId()) {
            case R.id.login:
                if (stringTest()) {
                    intent.putExtra("type", "/Login ");
                }
                break;
            case R.id.register:
                if (stringTest()) {
                    intent.putExtra("type", "/Register ");
                }
                break;
            case R.id.exit:
                isExit.show();
                return;
        }

        intent.putExtra("username", username);
        intent.putExtra("password", password);

        startService(intent);

        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isExit.show();
        }

        return false;
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                if (FiveChessService.isConnect()) {
                    unbindService(connection);

                    Intent intent = new Intent(MainActivity.this, FiveChessService.class);
                    stopService(intent);
                }

                finish();
            }
        }
    };

    private void init() {
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

        isExit = new AlertDialog.Builder(this).create();

        isExit.setTitle("系统提示");
        isExit.setMessage("确定要退出吗");

        isExit.setButton(AlertDialog.BUTTON_POSITIVE, "确定", listener);
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", listener);
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
            default:
                if (userName.getText().toString().equals(list[0])) {
                    UserInformation user = new UserInformation(list[2], list[3], list[4], list[5]);
                    binder.getService().setUser(user);

                    unbindService(connection);

                    Intent intent = new Intent(this, GameHallActivity.class);
                    startActivity(intent);

                    finish();
                }
                break;
        }
    }

    private boolean stringTest() {
        String pattern = String.format("^[a-z0-9A-Z]{%d,%d}$", 4, 10);
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(userName.getText().toString());

        if (!mat.find()) {
            Toast.makeText(MainActivity.this, "用户名无效, 请输入长度在4-10位之间的字母与数字组合", Toast.LENGTH_SHORT).show();
            return false;
        }

        pattern = String.format("^[a-z0-9A-Z]{%d,%d}$", 6, 10);
        pat = Pattern.compile(pattern);
        mat = pat.matcher(passWord.getText().toString());

        if (!mat.find()) {
            Toast.makeText(MainActivity.this, "密码无效, 请输入长度在6-10位之间的字母与数字组合 ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
