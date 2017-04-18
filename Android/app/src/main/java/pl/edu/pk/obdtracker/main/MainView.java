package pl.edu.pk.obdtracker.main;

import android.bluetooth.BluetoothDevice;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.Map;

import pl.edu.pk.obdtracker.dialog.ChooseBtDeviceDialogFragment;

/**
 * @author Wojciech Kocik
 * @since 12.03.2017
 */

interface MainView extends MvpView {
    void setStartProducerButtonEnabled(boolean enabled);

    void showChooseBtDeviceDialog(ChooseBtDeviceDialogFragment.BluetoothDeviceListener listener);

    void showRetrievingBtDeviceProgress();

    void hideRetrievingBtDeviceProgress();

    void setSelectedDeviceInformation(BluetoothDevice bluetoothDevice);

    void showUnsuccessfulConnectionInfo();

    void changeTextAndHandlerForNavBtConnectionStop();

    void setInitMessageForChoosingDevice();

    void saveLogcatToFile();

    void showObdData(Map<String, String> obdData);

    void showServiceConnected();

    void showServiceDisconnected();

    void showGeneratedAccountIdInfo(String accountId);

    void showGeneratingAccountIdProgress();

    void hideGeneratingAccountIdProgress();
}
