package pl.edu.pk.obdtracker;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import pl.edu.pk.obdtracker.obd.concurrency.ObdBluetoothService;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Singleton
@Component(modules = {
        AppModule.class,
        BluetoothModule.class
})
public interface ServiceComponent {
    void inject(ObdBluetoothService obdBluetoothService);

    SharedPreferences sharedPreferences();
}
