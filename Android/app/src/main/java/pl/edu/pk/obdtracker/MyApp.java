package pl.edu.pk.obdtracker;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

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

    public void onCreate() {
        super.onCreate();

        MvpModule mvpModule = new MvpModule();
        AppModule appModule = new AppModule(this);

        mvpComponent = DaggerMvpComponent.builder()
                .appModule(appModule)
                .mvpModule(mvpModule)
                .build();

        serviceComponent = DaggerServiceComponent.builder()
                .appModule(appModule)
                .build();
    }


}
