package pl.edu.pk.obdtracker;

import android.content.SharedPreferences;

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

    @Singleton
    @Provides
    MainPresenter providesMainPresenter(SharedPreferences sharedPreferences){
        return new MainPresenter(sharedPreferences);
    }
}
