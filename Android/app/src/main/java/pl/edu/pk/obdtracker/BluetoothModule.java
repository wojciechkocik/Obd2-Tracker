package pl.edu.pk.obdtracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.pk.obdtracker.bluetooth.BluetoothManager;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Module
public class BluetoothModule {

    @Singleton
    @Provides
    BluetoothManager providesBluetoothManager(){
        return new BluetoothManager();
    }
}
