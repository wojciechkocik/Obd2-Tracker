package pl.edu.pk.obdtracker;

import javax.inject.Singleton;

import dagger.Component;
import pl.edu.pk.obdtracker.api.HttpService;
import pl.edu.pk.obdtracker.main.MainActivity;
import pl.edu.pk.obdtracker.main.MainPresenter;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

@Singleton
@Component(modules = {AppModule.class, MvpModule.class, HttpServiceModule.class})
public interface MvpComponent {
    void inject(MainActivity activity);
    MainPresenter mainPresenter();
    HttpService httpService();

}
