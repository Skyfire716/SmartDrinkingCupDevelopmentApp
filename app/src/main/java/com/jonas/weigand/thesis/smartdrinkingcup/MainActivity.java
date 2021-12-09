package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IDeviceHandler, IDeviceDiscovered, IAD5932ConfigChanged {

    protected AD5932Fragment ad5932Fragment;

    protected ScanDeviceFragment scanDeviceFragment;
    protected BluetoothAdapter bluetoothAdapter;

    protected BluetoothGatt ad5932Gatt;
    protected BluetoothGattService ad5932configService;


    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Add Device to list
            scanDeviceFragment.discoveredDevice(result.getDevice());
            //Log.d("Bluetooth", "Found Device " + result.getDevice().getName());
        }
    };

    protected BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler;
    private static final long SCAN_PERIOD = 12000;
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("Bluetooth", "Connected");
                gatt.discoverServices();
                connectedToDevice();
                // successfully connected to the GATT Server
                //connectionState = STATE_CONNECTED;
                //bluetoothLeService.broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                //connectionState = STATE_DISCONNECTED;
                //broadcastUpdate(ACTION_GATT_DISCONNECTED);
                disconnectedToDevice();
                Log.d("Bluetooth", "Disconnected");
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("Characteristic", "Read");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d("Characteristic", "Changed");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("Characteristic", "Wrote");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                ArrayList<BluetoothGattService> services = new ArrayList<>(gatt.getServices());
                if (services.isEmpty()) {
                    Log.d("Bluetooth", "Discovered empty service");
                }
                for (BluetoothGattService service : services) {
                    Log.d("Bluetooth", "Found Service " + service.getUuid());
                    if (service.getUuid().equals(ApplicationUUIDS.UUID_CONFIGURATION)){
                        Log.d("Configuration", "Service!");
                        Log.d("Configuration", "Found n characteristics " + service.getCharacteristics().size());
                        ad5932Gatt = gatt;
                        ad5932configService = service;
                    }
                    ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>(service.getCharacteristics());
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        characteristic.getPermissions();
                        Log.d("Bluetooth", "Permission Read? " + ((characteristic.getPermissions() & BluetoothGattCharacteristic.PERMISSION_READ) == BluetoothGattCharacteristic.PERMISSION_READ));
                        Log.d("Bluetooth", "Permission Write? " + ((characteristic.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE) == BluetoothGattCharacteristic.PERMISSION_WRITE));
                    }

                }
            } else {
                Log.w("Bluetooth", "onServicesDiscovered received: " + status);
            }

        }
    };


    private static final int REQUEST_BT_ENABLE = 1;
    private boolean waitForDiscoveryStop = false;
    private boolean discovering = false;
    private BluetoothDevice device;

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(getResources().getString(R.string.hardware_error), "Device has no BluetoothAdapter");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            }
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.flushPendingScanResults(leScanCallback);
        Log.d("Bluetooth", "BluetoothLEScanner " + bluetoothLeScanner);
        handler = new Handler();
        scanDevice(750);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == RESULT_OK) {
                Log.d(getResources().getString(R.string.bluetooth_debug), "Bluetooth garanted");
            } else {
                Log.d(getResources().getString(R.string.bluetooth_debug), "Bluetooth request results in: " + resultCode);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        device.connectGatt(this, false, bluetoothGattCallback);
        /*
        bluetoothClientManager = new BluetoothClientManager(device, getApplicationContext());
        bluetoothClientManager.start();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ad5932Fragment = new AD5932Fragment();
        transaction.replace(R.id.deviceViewer, ad5932Fragment);
        transaction.commit();
        ad5932Fragment.setBluetoothClient(bluetoothClientManager);
        */
        Log.d("Bluetooth", "Connect to device");
        /*
        Log.d(getResources().getString(R.string.bluetooth_debug), "Connecting to BluetoothDevice");
        bluetoothAdapter.cancelDiscovery();
        this.device = device;
        waitForDiscoveryStop = true;
        if (!discovering){
            discoveryFinished();
        }
        */
    }

    @Override
    public void connectedToDevice() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ad5932Fragment = new AD5932Fragment();
        transaction.replace(R.id.deviceViewer, ad5932Fragment);
        transaction.commit();
        if (ad5932Fragment == null){
            Log.d("ADFragment", "Is null");
        }else {
            ad5932Fragment.setAD5932ConfigChanged(this);
        }
    }

    @Override
    public void disconnectedToDevice() {

    }

    @Override
    public void scanDevice(int delay) {
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //bluetoothAdapter.startDiscovery();
                scanLeDevice();
            }
        }, delay);
    }


    @Override
    public void discoveredDevice(BluetoothDevice device) {

    }

    @Override
    public void discoveryStarted() {

    }

    @Override
    public void discoveryFinished() {

    }

    private void scanLeDevice() {
        if (!scanning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Log.d("Bluetooth", "Stopped Scan");
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d("Bluetooth", "Started Scan");
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d("Bluetooth", "Stopped Scan");
        }
    }

    @Override
    public void changeConfig(AD5932Config conf) {
        if (ad5932Gatt != null && ad5932configService != null){
            for (BluetoothGattCharacteristic characteristic: ad5932configService.getCharacteristics()) {
                byte[] data = conf.packConfigForTransfer();
                for (int i = 0; i < 14; i++) {
                    Log.d("Data", "" + Integer.toBinaryString((data[i] & 0xFF)));
                }
                characteristic.setValue(data);
                ad5932Gatt.writeCharacteristic(characteristic);
                Log.d("Status", "Wrote to device");
            }
        }
        Log.d("Called", "Write");
    }
}