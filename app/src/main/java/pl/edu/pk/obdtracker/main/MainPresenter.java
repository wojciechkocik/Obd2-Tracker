package pl.edu.pk.obdtracker.main;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.MvpAvareBasePresenter;
import pl.edu.pk.obdtracker.dialog.ChooseBtDeviceDialogFragment;
import pl.edu.pk.obdtracker.event.ObdJobEvent;
import pl.edu.pk.obdtracker.obd.concurrency.ObdBluetoothService;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Slf4j
public class MainPresenter extends MvpAvareBasePresenter<MainView> {

    private final SharedPreferences sharedPreferences;

    @Getter
    private boolean isServiceBound;

    @Getter
    private ObdBluetoothService mObdBluetoothService;

    private BluetoothDevice mBluetoothDevice;

    @Inject
    public MainPresenter(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public ServiceConnection serviceConnection() {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                log.info(name.toString() + " service is bound");
                isServiceBound = true;
                mObdBluetoothService = ((ObdBluetoothService.ObdBluetoothServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                log.info(name.toString() + " service is unbound");
            }
        };
        return serviceConnection;
    }

    public void retrieveBluetoothDevice() {

        getView().showRetrievingBtDeviceProgress();

        getView().showChooseBtDeviceDialog(new ChooseBtDeviceDialogFragment.BluetoothDeviceListener() {
            @Override
            public void onDeviceChoose(BluetoothDevice bluetoothDevice) {
                log.info("Choose {} device, id: {}", bluetoothDevice.getName());
                mBluetoothDevice = bluetoothDevice;
                getView().hideRetrievingBtDeviceProgress();

                getView().setSelectedDeviceInformation(bluetoothDevice);

            }
        });
    }

    public void bluetoothConnect() {
        try {
            mObdBluetoothService.setBluetoothDevice(mBluetoothDevice);
            mObdBluetoothService.startService();
        } catch (IOException e) {
            log.error("Unsuccessful connection with bluetooth device.");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onObdJob(ObdJobEvent obdJobEvent) {
        log.info(obdJobEvent.getObdCommandJob().getObdCommand().getFormattedResult());
    }
}
