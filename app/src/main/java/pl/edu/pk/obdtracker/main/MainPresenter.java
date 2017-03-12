package pl.edu.pk.obdtracker.main;

import android.content.SharedPreferences;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

public class MainPresenter extends MvpBasePresenter<MainView> {
    private final SharedPreferences sharedPreferences;

    @Inject
    public MainPresenter(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
