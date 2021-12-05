package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button refreshDevicesBtn;
    protected CardView devicesViewer;

    protected BluetoothAdapter bluetoothAdapter;

    protected BlutoothController broadcastSink;

    private static final int REQUEST_BT_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshDevicesBtn = findViewById(R.id.refreshDevicesBtn);
        devicesViewer = findViewById(R.id.deviceViewer);

        refreshDevicesBtn.setOnClickListener(this);
        devicesViewer.setOnClickListener(this);

        broadcastSink = new BlutoothController();
        registerReceiver(broadcastSink, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(broadcastSink, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Log.e(String.valueOf(R.string.hardware_error), "Device has no BluetoothAdapter");
        }else{
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            }
        }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_BT_ENABLE){
            if(resultCode == RESULT_OK) {
                Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth garanted");
            }else{
                Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth request results in: " + resultCode);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == refreshDevicesBtn){
            Log.d(String.valueOf(R.string.ui_interaction), "RefreshBtn Clicked");
            bluetoothAdapter.startDiscovery();
            //TODO Clear List of Devices
        }else if(v == devicesViewer) {
            Log.d(String.valueOf(R.string.ui_interaction), "CardView Item Clicked");
            //TODO Device Viewer zu RecyclerViewer machen
        }else{
            Log.d(String.valueOf(R.string.ui_interaction), "Unknown View clicked: " + v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastSink);
    }
}