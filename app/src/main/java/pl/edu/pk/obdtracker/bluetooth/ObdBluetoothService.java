package pl.edu.pk.obdtracker.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.Config;
import pl.edu.pk.obdtracker.MyApp;
import pl.edu.pk.obdtracker.main.MainActivity;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Slf4j
public class ObdBluetoothService extends Service {

    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();
    protected long queueCounter = 0;

    private boolean isRunning;

    private SharedPreferences sharedPreferences;

    @Setter
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;

    Thread queueWorkerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                executeQueue();
            } catch (InterruptedException e) {
                queueWorkerThread.interrupt();
            }
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = ((MyApp) getApplication()).getServiceComponent().sharedPreferences();
        queueWorkerThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.debug("Destroying service...");
        queueWorkerThread.interrupt();
        log.debug("Service destroyed.");
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
            startObdConnection(bluetoothDevice);
        } catch (Exception e) {
            log.error(
                    "There was an error while establishing connection. -> "
                            + e.getMessage()
            );

            // in case of failure, stop this service.
            stopService();
            throw new IOException();
        }

//        if (remoteDevice == null || remoteDevice.isEmpty()) {
//            Toast.makeText(getApplicationContext(), getString(R.string.text_bluetooth_nodevice), Toast.LENGTH_LONG).show();
//
//            // log error
//            log.error("No Bluetooth device has been selected.");
//            throw new IOException();
//        } else {
//            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//            bluetoothDevice = btAdapter.getRemoteDevice(remoteDevice);
//        }


    }

    /**
     * This method will add a job to the queue while setting its ID to the
     * internal queue counter.
     *
     * @param job the job to queue.
     */
    public void queueJob(ObdCommandJob job) {

        job.getObdCommand().useImperialUnits(sharedPreferences.getBoolean(Config.IMPERIAL_UNITS_KEY, false));

        queueCounter++;
        log.debug("Adding job[" + queueCounter + "] to queue..");

        job.setId(queueCounter);
        try {
            jobsQueue.put(job);
            log.debug("Job queued successfully.");
        } catch (InterruptedException e) {
            job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.QUEUE_ERROR);
            log.debug("Failed to queue job.");
        }
    }

    /**
     * Runs the queue until the service is stopped
     */
    protected void executeQueue() throws InterruptedException {
        log.debug("Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();

                // log job
                log.debug("Taking job[" + job.getId() + "] from queue..");

                if (job.getObdCommandJobState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    log.debug("Job state is NEW. Run it..");
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    if (socket.isConnected()) {
                        job.getObdCommand().run(socket.getInputStream(), socket.getOutputStream());
                    } else {
                        job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                        log.debug("Can't run command on a closed socket.");
                    }
                } else
                    // log not new job
                    log.debug(
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                log.debug("Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if (io.getMessage().contains("Broken pipe"))
                        job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    else
                        job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                log.debug("IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                log.debug("Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                final ObdCommandJob job2 = job;
//                ((MainActivity) getApplicationContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        ((MainActivity) getApplicationContext()).stateUpdate(job2);
//                    }
//                });
            }
        }
    }

    private void startObdConnection(BluetoothDevice bluetoothDevice) throws IOException {
        log.debug("Starting OBD connection..");
        isRunning = true;
        try {
            socket = BluetoothManager.connect(bluetoothDevice);
        } catch (Exception e2) {
            log.error("There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }

        // Let's configure the connection.
        log.debug("Queueing jobs for connection configuration..");
        queueJob(new ObdCommandJob(new ObdResetCommand()));

        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        queueJob(new ObdCommandJob(new EchoOffCommand()));

    /*
     * Will send second-time based on tests.
     *
     * TODO this can be done w/o having to queue jobs by just issuing
     * command.run(), command.getResult() and validate the result.
     */
        queueJob(new ObdCommandJob(new EchoOffCommand()));
        queueJob(new ObdCommandJob(new LineFeedOffCommand()));
        queueJob(new ObdCommandJob(new TimeoutCommand(62)));

        // Get protocol from preferences
        final String protocol = sharedPreferences.getString(Config.PROTOCOLS_LIST_KEY, "AUTO");
        queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocol))));

        // Job for returning dummy data
        queueJob(new ObdCommandJob(new AmbientAirTemperatureCommand()));

        queueCounter = 0L;
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

    public class ObdBluetoothServiceBinder extends Binder {
        public ObdBluetoothService getService() {
            return ObdBluetoothService.this;
        }
    }
}
