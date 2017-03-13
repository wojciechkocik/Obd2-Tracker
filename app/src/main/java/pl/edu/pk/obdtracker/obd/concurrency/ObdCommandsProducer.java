package pl.edu.pk.obdtracker.obd.concurrency;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;

import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */

@Slf4j
class ObdCommandsProducer extends Thread {

    private BlockingQueue<ObdCommandJob> jobsQueue;
    private long queueCounter = 0;


    public ObdCommandsProducer(BlockingQueue<ObdCommandJob> jobsQueue) {
        this.jobsQueue = jobsQueue;
    }

    private void queueCommands() {
        queueJob(new SpeedCommand());
        queueJob(new RPMCommand());
    }

    @Override
    public void run() {
        queueCommands();
    }

    public void queueJob(ObdCommand job) {

        ObdCommandJob obdCommandJob = new ObdCommandJob(job);

//        job.getObdCommand().useImperialUnits(sharedPreferences.getBoolean(Config.IMPERIAL_UNITS_KEY, false));

        queueCounter++;
        log.debug("Adding job[" + queueCounter + "] to queue..");

        obdCommandJob.setId(queueCounter);
        try {
            jobsQueue.put(obdCommandJob);
            log.debug("Job queued successfully.");
        } catch (InterruptedException e) {
            obdCommandJob.setObdCommandJobState(ObdCommandJob.ObdCommandJobState.QUEUE_ERROR);
            log.debug("Failed to queue job.");
        }
    }

    public void resetQueueCounter() {
        queueCounter = 0L;
    }
}
