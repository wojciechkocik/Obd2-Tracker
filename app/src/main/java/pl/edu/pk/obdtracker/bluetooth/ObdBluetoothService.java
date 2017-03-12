package pl.edu.pk.obdtracker.bluetooth;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

public class ObdBluetoothService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public void startService(){

    }
}
