package com.fatesolo.fivechessandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import de.greenrobot.event.EventBus;

public class GameHallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_hall);

        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void init() {

    }

    public void onEventMainThread(String msg) {

    }

}
