package pl.edu.pk.obdtracker.obd.concurrency;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

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
//        queueJob(new SpeedCommand());
//        queueJob(new RPMCommand());
//        queueJob(new VinCommand());
//        queueJob(new TroubleCodesCommand());


        // Engine
        queueJob(new LoadCommand());
        queueJob(new RPMCommand());
        queueJob(new RuntimeCommand());
        queueJob(new MassAirFlowCommand());
        queueJob(new ThrottlePositionCommand());

        // Fuel
//        queueJob(new FindFuelTypeCommand());
        queueJob(new ConsumptionRateCommand());
        // queueJob(new AverageFuelEconomyObdCommand());
        //queueJob(new FuelEconomyCommand());
        queueJob(new FuelLevelCommand());
        // queueJob(new FuelEconomyMAPObdCommand());
        // queueJob(new FuelEconomyCommandedMAPObdCommand());
//        queueJob(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
//        queueJob(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
//        queueJob(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
//        queueJob(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        queueJob(new AirFuelRatioCommand());
//        queueJob(new WidebandAirFuelRatioCommand());
        queueJob(new OilTempCommand());

        // Pressure
        queueJob(new BarometricPressureCommand());
//        queueJob(new FuelPressureCommand());/
//        queueJob(new FuelRailPressureCommand());
//        queueJob(new IntakeManifoldPressureCommand());

        // Temperature
        queueJob(new AirIntakeTemperatureCommand());
//        queueJob(new AmbientAirTemperatureCommand());
        queueJob(new EngineCoolantTemperatureCommand());

        // Misc
        queueJob(new SpeedCommand());

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
