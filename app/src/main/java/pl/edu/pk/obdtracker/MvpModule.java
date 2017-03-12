package pl.edu.pk.obdtracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.pk.obdtracker.main.MainPresenter;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Module
public class MvpModule {

//    @Provides
//    @Singleton
//        // Application reference must come from AppModule.class
//    SharedPreferences providesSharedPreferences(Application application) {
//        return PreferenceManager.getDefaultSharedPreferences(application);
//    }

    @Singleton
    @Provides
    MainPresenter providesMainPresenter(SharedPreferences sharedPreferences){
        return new MainPresenter(sharedPreferences);
    }
}
