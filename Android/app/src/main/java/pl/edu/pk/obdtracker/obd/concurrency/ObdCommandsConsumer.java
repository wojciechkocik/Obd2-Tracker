package pl.edu.pk.obdtracker.obd.concurrency;

import com.github.pires.obd.exceptions.UnsupportedCommandException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.api.DataStorageHttpService;
import pl.edu.pk.obdtracker.api.model.ObdData;
import pl.edu.pk.obdtracker.bluetooth.BluetoothReaderObserver;
import pl.edu.pk.obdtracker.event.ObdJobEvent;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */

@Slf4j
class ObdCommandsConsumer extends Thread {

    private BlockingQueue<ObdCommandJob> jobsQueue;
    private BluetoothReaderObserver bluetoothReader;
    private DataStorageHttpService dataStorageHttpService;

    public ObdCommandsConsumer(BluetoothReaderObserver bluetoothReader, BlockingQueue<ObdCommandJob> jobsQueue, DataStorageHttpService dataStorageHttpService) {
        this.bluetoothReader = bluetoothReader;
        this.jobsQueue = jobsQueue;
        this.dataStorageHttpService = dataStorageHttpService;
    }

    @Override
    public void run() {
        try {
            executeQueue();
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

    private void executeQueue() throws InterruptedException {
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
                    bluetoothReader.read(job);

                    if (job != null) {
                        final ObdCommandJob job2 = job;
                        ObdJobEvent obdJobEvent = new ObdJobEvent();
                        obdJobEvent.setObdCommandJob(job2);

                        String name = obdJobEvent.getObdCommandJob().getObdCommand().getName();
                        String formattedResult = obdJobEvent.getObdCommandJob().getObdCommand().getCalculatedResult();

                        ObdData obdData = new ObdData();
                        obdData.setLabel(name);
                        obdData.setValue(formattedResult);
                        obdData.setEpoch(System.currentTimeMillis());
                        obdData.setAccountId("TODO");
                        storeObdData(obdData);

                        EventBus.getDefault().post(obdJobEvent);
                    }

                } else
                    // log not new job
                    log.info(
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                log.info("Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if (io.getMessage().contains("Broken pipe"))
                        job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    else
                        job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                log.info("IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                log.info("Failed to run command. -> " + e.getMessage());
            }
        }


    }

    private void storeObdData(ObdData obdData) throws IOException {
        Call<Void> voidCall = dataStorageHttpService.storeData(obdData);
        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                log.error("Store data to http api error");
            }
        });
    }
}
