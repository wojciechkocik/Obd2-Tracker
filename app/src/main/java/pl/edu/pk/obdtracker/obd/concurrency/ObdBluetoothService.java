package pl.edu.pk.obdtracker.obd.concurrency;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.EquivalentRatioCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.Config;
import pl.edu.pk.obdtracker.IAidlInterface;
import pl.edu.pk.obdtracker.MyApp;
import pl.edu.pk.obdtracker.R;
import pl.edu.pk.obdtracker.bluetooth.BluetoothManager;
import pl.edu.pk.obdtracker.bluetooth.BluetoothReaderObserver;
import pl.edu.pk.obdtracker.main.MainActivity;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Slf4j
public class ObdBluetoothService extends Service implements BluetoothReaderObserver {

    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();

    private ScheduledExecutorService producerExecutorService;

    private boolean isRunning;

    private SharedPreferences sharedPreferences;

    @Setter
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;

    private ObdCommandsProducer obdCommandsProducer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            not = new Notification.Builder(this).
                    setContentTitle(getText(R.string.app_name)).
                    setContentText("Doing stuff in the background...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher).
                    build();
        }
        startForeground(1, not);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ignored) {
        }

        sharedPreferences = ((MyApp) getApplication()).getServiceComponent().sharedPreferences();


        obdCommandsProducer = new ObdCommandsProducer(jobsQueue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        log.debug("Destroying service...");
        EventBus.getDefault().unregister(this);
//        log.debug("Service destroyed.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ObdBluetoothServiceBinder();
    }

    public void startService() throws IOException {
        log.info("Starting obd bluetooth service");
        final String remoteDevice = sharedPreferences.getString(Config.BLUETOOTH_LIST_KEY, null);

        try {
            socket = startBluetoothConnection(bluetoothDevice);
            ObdCommandsConsumer obdCommandsConsumer = new ObdCommandsConsumer(this, jobsQueue);
            obdCommandsConsumer.start();
            obdConnectionInit();
            producerExecutorService = new ScheduledThreadPoolExecutor(1);
            producerExecutorService.scheduleAtFixedRate(obdCommandsProducer, 0, 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(
                    "There was an error while establishing connection. -> "
                            + e.getMessage()
            );

            // in case of failure, stop this service.
            stopService();
            throw new IOException();
        }
    }

    private void queueJob(ObdCommand job) {
        obdCommandsProducer.queueJob(job);
    }

    private BluetoothSocket startBluetoothConnection(BluetoothDevice bluetoothDevice) throws IOException {
        log.debug("Starting OBD connection..");
        isRunning = true;
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = BluetoothManager.connect(bluetoothDevice);
        } catch (Exception e2) {
            log.error("There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }

        return bluetoothSocket;
    }

    public void resetObd(){
        queueJob(new ObdResetCommand());
    }

    private void obdConnectionInit() {
        // Let's configure the connection.
        log.debug("Queueing jobs for connection configuration..");

        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        queueJob(new EchoOffCommand());

    /*
     * Will read second-time based on tests.
     *
     * TODO this can be done w/o having to queue jobs by just issuing
     * command.run(), command.getResult() and validate the result.
     */
        queueJob(new EchoOffCommand());
        queueJob(new LineFeedOffCommand());
        queueJob(new TimeoutCommand(62));

        // Get protocol from preferences
        final String protocol = sharedPreferences.getString(Config.PROTOCOLS_LIST_KEY, "AUTO");
        queueJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocol)));

        // Job for returning dummy data
        queueJob(new AmbientAirTemperatureCommand());

        // Control
//        queueJob(new ModuleVoltageCommand());
//        queueJob(new EquivalentRatioCommand());
//        queueJob(new DistanceMILOnCommand());
//        queueJob(new DtcNumberCommand());
//        queueJob(new TimingAdvanceCommand());
//        queueJob(new TroubleCodesCommand());
        queueJob(new VinCommand());

        obdCommandsProducer.resetQueueCounter();
        log.debug("Initialization jobs queued.");
    }

    /**
     * Stop OBD connection and queue processing.
     */
    public void stopService() {
        log.debug("Stopping service..");


//        notificationManager.cancel(NOTIFICATION_ID);
        jobsQueue.clear();
        isRunning = false;

        if (producerExecutorService != null) {
            producerExecutorService.shutdown();
        }

        if (socket != null)
            // close socket
            try {
                socket.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

        // kill service
        stopSelf();
    }

    @Override
    public void read(ObdCommandJob job) throws IOException, InterruptedException {
        if (socket.isConnected()) {
            job.getObdCommand().run(socket.getInputStream(), socket.getOutputStream());
        } else {
            job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
            log.debug("Can't run command on a closed socket.");
        }
    }

    public class ObdBluetoothServiceBinder extends Binder {
        public ObdBluetoothService getService() {
            return ObdBluetoothService.this;
        }
    }
}
