package pl.edu.pk.obdtracker;

import android.app.Application;

import lombok.Getter;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

public class MyApp extends Application {

    @Getter
    private MvpComponent mvpComponent;

    @Getter
    private ServiceComponent serviceComponent;

    private MvpModule mvpModule;

    public void onCreate() {
        super.onCreate();

        mvpModule = new MvpModule();
        BluetoothModule bluetoothModule = new BluetoothModule();
        AppModule appModule = new AppModule(this);

        mvpComponent = DaggerMvpComponent.builder()
                .appModule(appModule)
                .mvpModule(mvpModule)
                .bluetoothModule(bluetoothModule)
                .build();

        serviceComponent = DaggerServiceComponent.builder()
                .appModule(appModule)
                .bluetoothModule(bluetoothModule)
                .build();
    }
}
