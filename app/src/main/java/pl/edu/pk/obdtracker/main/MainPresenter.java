package pl.edu.pk.obdtracker.main;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

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
    boolean isProducerRunning;

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
                getView().showServiceConnected();
                isServiceBound = true;
                mObdBluetoothService = ((ObdBluetoothService.ObdBluetoothServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                log.info(name.toString() + " service is unbound");
                getView().showServiceDisconnected();
                isServiceBound = false;
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
                getView().setSelectedDeviceInformation(bluetoothDevice);
                bluetoothConnect();
            }
        });
    }

    public void resetObd() {
        mObdBluetoothService.resetObd();
    }

    public void bluetoothConnect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mObdBluetoothService.setBluetoothDevice(mBluetoothDevice);
                try {
                    mObdBluetoothService.startService();
                    getView().changeTextAndHandlerForNavBtConnectionStop();
                    getView().setSelectedDeviceInformation(mBluetoothDevice);
                    getView().hideRetrievingBtDeviceProgress();
                    getView().setStartProducerButtonEnabled(true);
                } catch (IOException e) {
                    log.error("Unsuccessful connection with bluetooth device.");
                    mObdBluetoothService.stopService();
                    getView().changeTextAndHandlerForNavBtConnectionStop();
                    getView().showUnsuccessfulConnectionInfo();
                    getView().hideRetrievingBtDeviceProgress();
                    getView().saveLogcatToFile();
                }
            }
        })
                .start();
    }

    private Map<String, String> obdData = new HashMap<>();

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onObdJob(ObdJobEvent obdJobEvent) {

        String name = obdJobEvent.getObdCommandJob().getObdCommand().getName();
        String result = obdJobEvent.getObdCommandJob().getObdCommand().getResult();

        if(result == null){

        }
        else if (result.equals("NODATA")) {
            log.debug(name + ": " + obdJobEvent.getObdCommandJob().getObdCommand().getResult());
        } else {
            String formattedResult = obdJobEvent.getObdCommandJob().getObdCommand().getFormattedResult();
            log.info(name + ": " + formattedResult);
            obdData.put(name, formattedResult + "  :::  " + new Date().getTime());
            getView().showObdData(obdData);
        }
    }

    public void disconnectCurrentDevice() {
        isServiceBound = false;
        mObdBluetoothService.stopService();
        getView().setInitMessageForChoosingDevice();
        getView().saveLogcatToFile();
    }

    public void startObdQueueJobProducer(){
        getMObdBluetoothService().startProducer();
        isProducerRunning = true;
    }
}
