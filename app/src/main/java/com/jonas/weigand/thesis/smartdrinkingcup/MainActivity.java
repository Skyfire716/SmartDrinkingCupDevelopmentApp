package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IDeviceHandler, IDeviceDiscovered, IAD5932ConfigChanged {

    protected AD5932Fragment ad5932Fragment;

    protected ScanDeviceFragment scanDeviceFragment;
    protected BluetoothAdapter bluetoothAdapter;

    protected String deviceName;

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
    private static final long SCAN_PERIOD = 7000;
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
                Log.d("Bluetooth", "Disconnected");
                Log.d("Bluetooth", "Disconnected");
                Log.d("Bluetooth", "Disconnected");
                Log.d("Bluetooth", "Disconnected");
                Log.d("Bluetooth", "Disconnected");

                disconnectedToDevice();
                Log.d("Bluetooth", "Disconnected");
            } else {
                printGattStatus(status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("Characteristic", "Read");
            printGattStatus(status);
            Log.d("Characteristic", "Read");
            Log.d("Values", "" + characteristic.getValue());
            if (characteristic.getValue() != null && characteristic.getUuid().equals(ApplicationUUIDS.UUID_CONFIGURATION)) {
                for (int i = 0; i < characteristic.getValue().length; i+=2) {
                    short a = (short) (((((short)characteristic.getValue()[i+1]) << 8) & 0xFF00) | (((short)characteristic.getValue()[i]) & 0xFF));
                    Log.d("Byte", "" + Integer.toBinaryString((a & 0xFFFF)));
                }
                AD5932Config ad5932Config = new AD5932Config();
                Log.d("Can Load From Data?", "" + ad5932Config.loadConfigFromTransfer(characteristic.getValue()));
                Log.d("AD5932Config", ad5932Config.toString());
                ad5932Fragment.setConfig(ad5932Config);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d("Characteristic", "Changed");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            printGattStatus(status);
            Log.d("Characteristic", "Wrote " + status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            printGattStatus(status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                ArrayList<BluetoothGattService> services = new ArrayList<>(gatt.getServices());
                if (services.isEmpty()) {
                    Log.d("Bluetooth", "Discovered empty service");
                }
                for (BluetoothGattService service : services) {
                    Log.d("Bluetooth", "Found Service " + service.getUuid());
                    if (service.getUuid().equals(ApplicationUUIDS.UUID_CONFIGURATION)) {
                        ad5932Gatt = gatt;
                        ad5932configService = service;
                    }
                }
                readAD5932Config();
            } else {
                Log.w("Bluetooth", "onServicesDiscovered received: " + status);
            }
        }
    };


    private void printGattStatus(int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                Log.d("GATTStatus", "GATT_SUCCESS");
                break;
            case BluetoothGatt.GATT_FAILURE:
                Log.d("GATTStatus", "GATT_FAILURE");
                break;
            case BluetoothGatt.GATT_CONNECTION_CONGESTED:
                Log.d("GATTStatus", "GATT_CONNECTION_CONGESTED");
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                Log.d("GATTStatus", "GATT_INSUFFICIENT_AUTHENTICATION");
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                Log.d("GATTStatus", "GATT_INSUFFICIENT_ENCRYPTION");
                break;
            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
                Log.d("GATTStatus", "GATT_INVALID_ATTRIBUTE_LENGTH");
                break;
            case BluetoothGatt.GATT_INVALID_OFFSET:
                Log.d("GATTStatus", "GATT_INVALID_OFFSET");
                break;
            case BluetoothGatt.GATT_READ_NOT_PERMITTED:
                Log.d("GATTStatus", "GATT_READ_NOT_PERMITTED");
                break;
            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
                Log.d("GATTStatus", "GATT_REQUEST_NOT_SUPPORTED");
                break;
            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                Log.d("GATTStatus", "GATT_WRITE_NOT_PERMITTED");
                break;
        }
    }

    private static final int REQUEST_BT_ENABLE = 1;
    private static final int REQUEST_LOCATION = 2;
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
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permission Missing", "Missing Permission to use: " + permissions[i]);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void connectToDevice(BluetoothDevice device) {
        deviceName = device.getName();
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
        if (ad5932Fragment == null) {
            Log.d("ADFragment", "Is null");
        } else {
            ad5932Fragment.setAD5932ConfigChanged(this);
            ad5932Fragment.setDeviceText(deviceName);
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
                    scanDeviceFragment.discoveryFinished();
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d("Bluetooth", "Started Scan");
            scanDeviceFragment.discoveryStarted();
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d("Bluetooth", "Stopped Scan");
            scanDeviceFragment.discoveryFinished();
        }
    }

    @Override
    public void changeConfig(AD5932Config conf) {
        if (ad5932Gatt != null && ad5932configService != null) {
            for (BluetoothGattCharacteristic characteristic : ad5932configService.getCharacteristics()) {
                Log.d("Characteristic", "" + characteristic.getUuid());
                byte[] data = conf.packConfigForTransfer();
                Log.d("WriteToLocalCharacteristic", "? " + characteristic.setValue(data));
                Log.d("WriteToRemoteInitiated", "? " + ad5932Gatt.writeCharacteristic(characteristic));
            }
        } else {
            if (ad5932Gatt == null) {
                Log.d("Is Null", "AD5932Gatt");
            }
            if (ad5932configService == null) {
                Log.d("Is Null", "ConfigService");
            }
        }
        Log.d("Called", "Write");
    }

    private void switchToScanFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        scanDeviceFragment = new ScanDeviceFragment(this);
        transaction.replace(R.id.deviceViewer, scanDeviceFragment);
        transaction.commit();
    }

    public void readAD5932Config(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ad5932Gatt != null && ad5932configService != null){
                    ad5932Gatt.readCharacteristic(ad5932configService.getCharacteristic(ApplicationUUIDS.UUID_CONFIGURATION));
                }
            }
        }, 500);
    }

    @Override
    public void returnToScanDevice() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                switchToScanFragment();
            }
        }, 200);
    }
}