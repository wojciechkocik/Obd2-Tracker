package pl.edu.pk.obdtracker.main;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pk.obdtracker.MyApp;
import pl.edu.pk.obdtracker.R;
import pl.edu.pk.obdtracker.dialog.ChooseBtDeviceDialogFragment;
import pl.edu.pk.obdtracker.obd.concurrency.ObdBluetoothService;

@Slf4j
public class MainActivity extends MvpActivity<MainView, MainPresenter>
        implements NavigationView.OnNavigationItemSelectedListener, MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    SharedPreferences sharedPreferences;

    @BindView(R.id.connectedDeviceTextId)
    TextView connectedDeviceTextView;

    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProgressDialog mSettingBtDeviceProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mSettingBtDeviceProgressDialog = new ProgressDialog(this);
        mSettingBtDeviceProgressDialog.setMessage(getString(R.string.setting_bt_device));
        mSettingBtDeviceProgressDialog.setCancelable(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent obdBluetoothServiceIntent = new Intent(this, ObdBluetoothService.class);
        bindService(obdBluetoothServiceIntent, getPresenter().serviceConnection(), BIND_AUTO_CREATE);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return ((MyApp) getApplication()).getMvpComponent().mainPresenter();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth_choose) {
            getPresenter().retrieveBluetoothDevice();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showChooseBtDeviceDialog(ChooseBtDeviceDialogFragment.BluetoothDeviceListener listener) {
        ChooseBtDeviceDialogFragment chooseBtDeviceDialogFragment = new ChooseBtDeviceDialogFragment();
        chooseBtDeviceDialogFragment.setListener(listener);
        chooseBtDeviceDialogFragment.show(getFragmentManager(), TAG);
    }

    @Override
    public void showRetrievingBtDeviceProgress() {
         mSettingBtDeviceProgressDialog.show();
    }

    @Override
    public void hideRetrievingBtDeviceProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSettingBtDeviceProgressDialog.hide();
            }
        });
    }

    @Override
    public void setSelectedDeviceInformation(final BluetoothDevice bluetoothDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedDeviceTextView.setText(String.format("Selected device: %s", bluetoothDevice.getName()));
            }
        });
    }

    @Override
    public void showUnsuccessfulConnectionInfo() {
        Snackbar.make(getCurrentFocus(), "Bluetooth device connection error. Have you paired your device and the device is active?", BaseTransientBottomBar.LENGTH_INDEFINITE)
        .show();
    }

    @Override
    public void changeTextAndHandlerForNavBtConnectionStop() {
        final MenuItem navBluetoothConnect = navView.getMenu().findItem(R.id.nav_bluetooth_choose);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navBluetoothConnect.setTitle("Disconnect");
                navBluetoothConnect.setChecked(false);
                navBluetoothConnect.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        getPresenter().disconnectCurrentDevice();
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void setInitMessageForChoosingDevice() {
        final MenuItem navBluetoothConnect = navView.getMenu().findItem(R.id.nav_bluetooth_choose);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedDeviceTextView.setText(R.string.no_obdii_device_selected_select_your_device_from_menu);
                navBluetoothConnect.setTitle(R.string.choose_obdii_device);
                navBluetoothConnect.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        getPresenter().retrieveBluetoothDevice();
                        return true;
                    }
                });
            }
        });
    }
}
