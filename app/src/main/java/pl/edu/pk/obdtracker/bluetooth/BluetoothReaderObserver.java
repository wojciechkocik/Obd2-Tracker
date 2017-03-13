package pl.edu.pk.obdtracker.bluetooth;

import java.io.IOException;

import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 14.03.2017
 */

public interface BluetoothReaderObserver {
    void read(ObdCommandJob job) throws IOException, InterruptedException;
}
