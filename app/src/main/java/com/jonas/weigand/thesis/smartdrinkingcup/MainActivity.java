package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IDeviceHandler {

    protected AD5932Fragment ad5932Fragment;

    protected ScanDeviceFragment scanDeviceFragment;
    protected BluetoothAdapter bluetoothAdapter;

    protected BlutoothController broadcastSink;

    protected BluetoothClientManager bluetoothClientManager;

    private static final int REQUEST_BT_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            scanDeviceFragment = new ScanDeviceFragment(this);
            transaction.replace(R.id.deviceViewer, scanDeviceFragment);
            transaction.commit();
        }

        getSupportActionBar().hide();


        broadcastSink = new BlutoothController(scanDeviceFragment);
        registerReceiver(broadcastSink, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(broadcastSink, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(broadcastSink, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(broadcastSink, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Log.e(getResources().getString(R.string.hardware_error), "Device has no BluetoothAdapter");
        }else{
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            }
        }
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.startDiscovery();
            }
        }, 1500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_BT_ENABLE){
            if(resultCode == RESULT_OK) {
                Log.d(getResources().getString(R.string.bluetooth_debug), "Bluetooth garanted");
            }else{
                Log.d(getResources().getString(R.string.bluetooth_debug), "Bluetooth request results in: " + resultCode);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastSink);
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        Log.d(getResources().getString(R.string.bluetooth_debug), "Connecting to BluetoothDevice");
        bluetoothAdapter.cancelDiscovery();
        bluetoothClientManager = new BluetoothClientManager(device);
        bluetoothClientManager.start();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ad5932Fragment = new AD5932Fragment();
        transaction.replace(R.id.deviceViewer, ad5932Fragment);
        transaction.commit();
    }
}