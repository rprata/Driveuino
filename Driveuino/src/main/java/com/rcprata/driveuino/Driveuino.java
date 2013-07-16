package com.rcprata.driveuino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.rcprata.driveuino.core.AccelerometerService;
import com.rcprata.driveuino.core.BluetoothService;
import com.rcprata.driveuino.utils.Globals;
import com.rcprata.driveuino.views.DeviceListActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Driveuino extends Activity {

    private TextView axisX, axisY, axisZ;

    private PowerManager pm;
    private PowerManager.WakeLock wl;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService;
    private AccelerometerService mAccelerometerService;

    boolean stateConnection = false;

    NumberFormat f = new DecimalFormat("0.00");


    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Globals.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case Globals.STATE_CONNECTED:
                            stateConnection=true;
                            break;
                        case Globals.STATE_CONNECTING:
                            stateConnection=false;
                            break;
                        case Globals.STATE_LISTEN:
                        case Globals.STATE_NONE:
                            stateConnection=false;
                            break;
                    }
                    break;
                case Globals.MESSAGE_READ:
                    break;
                case Globals.MESSAGE_HANDLER_ACCELEROMETER:
                    float[] accelerometerValues = (float[]) msg.obj;
                    axisX.setText(getString(R.string.axis_x) + f.format(accelerometerValues[0]));
                    axisY.setText(getString(R.string.axis_y) + f.format(accelerometerValues[1]));
                    axisZ.setText(getString(R.string.axis_z) + f.format(accelerometerValues[2]));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        axisX = (TextView)findViewById(R.id.axis_x);
        axisY = (TextView)findViewById(R.id.axis_y);
        axisZ = (TextView)findViewById(R.id.axis_z);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, Globals.TAG);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.bt_not_enabled_leaving), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Globals.REQUEST_ENABLE_BT);
        } else {
            if(mBluetoothService == null){
                mBluetoothService = new BluetoothService(this,handler);
                setupConfigurations();
            }
        }

        mAccelerometerService = new AccelerometerService(this, handler);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccelerometerService.registerAccelerometerServiceListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAccelerometerService.unregisterAccelerometerServiceListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothService!=null) mBluetoothService.stop();
        mAccelerometerService.unregisterAccelerometerServiceListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driveuino, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Globals.REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                ensureDiscoverable();
                return true;
            case R.id.info:
                dialogInflate();
                return true;
        }
        return false;
    }

    private void dialogInflate() {
        //TODO: Implementar dialog;
    }

    private void ensureDiscoverable() {
        Log.d(Globals.TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void setupConfigurations() {

        if(mBluetoothService==null)
            mBluetoothService = new BluetoothService(this,handler);

    }

}