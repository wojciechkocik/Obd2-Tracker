package pl.edu.pk.obdtracker;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */

public class MvpAvareBasePresenter<V extends MvpView> extends MvpBasePresenter<V> {
    @Override
    public void attachView(V view) {
        super.attachView(view);
        try{
            EventBus.getDefault().register(this);
        }catch (Exception e){

        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
    }
}
