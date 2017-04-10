package pl.edu.pk.obdtracker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.R;

/**
 * @author Wojciech Kocik
 * @since 31.03.2017
 */
@Slf4j
public class ChooseBtDeviceDialogFragment extends DialogFragment {

    @Setter
    private BluetoothDeviceListener listener;

    public interface BluetoothDeviceListener {
        void onDeviceChoose(BluetoothDevice bluetoothDevice);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final List<BluetoothDevice> bondedDevices = new ArrayList<>(btAdapter.getBondedDevices());
        String[] boundedDeviceNames = new String[bondedDevices.size()];
        for (int i = 0; i < bondedDevices.size(); i++) {
            boundedDeviceNames[i] = bondedDevices.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_bt_device)
                .setCancelable(false)
                .setItems(boundedDeviceNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice bluetoothDevice = bondedDevices.get(which);
                        listener.onDeviceChoose(bluetoothDevice);
                    }
                });

        return builder.create();
    }
}
