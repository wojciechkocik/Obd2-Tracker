package pl.edu.pk.obdtracker.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.io.IOException;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.bluetooth.ObdBluetoothService;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Slf4j
public class MainPresenter extends MvpBasePresenter<MainView> {
    private final SharedPreferences sharedPreferences;

    @Getter
    private boolean isServiceBound;

    private ObdBluetoothService obdBluetoothService;

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
                obdBluetoothService = ((ObdBluetoothService.ObdBluetoothServiceBinder)service).getService();
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
        if(((BluetoothDevice)btAdapter.getBondedDevices().toArray()[2]).getName().equals("KOTMSI")){
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


}
