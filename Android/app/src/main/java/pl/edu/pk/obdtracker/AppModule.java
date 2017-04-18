package pl.edu.pk.obdtracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.pk.obdtracker.api.DataStorageHttpService;
import retrofit2.Retrofit;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Module
public class AppModule {

    Application mApplication;

    public AppModule(MyApp application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    EventBus providesEventBus(MyApp application) {
        return EventBus.getDefault();
    }

}
