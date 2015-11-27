package com.fatesolo.fivechessandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FiveChessService extends Service {
    public FiveChessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
