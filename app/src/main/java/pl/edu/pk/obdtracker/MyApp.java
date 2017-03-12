package pl.edu.pk.obdtracker;

import android.app.Application;

import javax.inject.Inject;

import pl.edu.pk.obdtracker.main.MainPresenter;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

public class MyApp extends Application {

    private MvpComponent mvpComponent;

    private MvpModule mvpModule;

    public void onCreate() {
        super.onCreate();

        mvpModule = new MvpModule();
        mvpComponent = DaggerMvpComponent.builder()
                .appModule(new AppModule(this))
                .mvpModule(mvpModule)
                .build();
    }

    public MvpModule getMvpModule(){
        return mvpModule;
    }

    public MvpComponent getMvpComponent(){
        return mvpComponent;
    }
}
