package pl.edu.pk.obdtracker.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import com.github.pires.obd.commands.SpeedCommand;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.MvpAvareBasePresenter;
import pl.edu.pk.obdtracker.bluetooth.ObdBluetoothService;
import pl.edu.pk.obdtracker.event.ObdJobEvent;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Slf4j
public class MainPresenter extends MvpAvareBasePresenter<MainView> {
    private static final long OBD_UPDATE_PERIOD = 1;
    private final SharedPreferences sharedPreferences;

    @Getter
    private boolean isServiceBound;

    private ObdBluetoothService obdBluetoothService;

    @Inject
    public MainPresenter(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Getter
    private final Runnable dataThreadQueue = new Runnable() {
        @Override
        public void run() {
            queueCommands();
            new Handler().postDelayed(dataThreadQueue, OBD_UPDATE_PERIOD);
        }
    };

    private void queueCommands() {
        obdBluetoothService.queueJob(new ObdCommandJob(new SpeedCommand()));
    }

    public ServiceConnection serviceConnection() {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                log.info(name.toString() + " service is bound");
                isServiceBound = true;
                obdBluetoothService = ((ObdBluetoothService.ObdBluetoothServiceBinder) service).getService();
                BluetoothDevice bluetoothDevice = retrieveBluetoothDevice();
                obdBluetoothService.setBluetoothDevice(bluetoothDevice);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                log.info(name.toString() + " service is unbound");
            }
        };
        return serviceConnection;
    }

    private BluetoothDevice retrieveBluetoothDevice() {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (((BluetoothDevice) btAdapter.getBondedDevices().toArray()[2]).getName().equals("KOTMSI")) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) btAdapter.getBondedDevices().toArray()[2];
            return bluetoothDevice;

        }
        return null;
        //TODO: selecting device
    }

    public void bluetoothConnect() {
        try {
            obdBluetoothService.startService();
        } catch (IOException e) {
            log.error("Unsuccessful connection with bluetooth device.");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onObdJob(ObdJobEvent obdJobEvent) {
        log.info(obdJobEvent.getObdCommandJob().getObdCommand().getFormattedResult());
    }

}
