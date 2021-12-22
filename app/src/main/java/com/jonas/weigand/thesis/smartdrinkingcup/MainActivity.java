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
import android.bluetooth.BluetoothGattDescriptor;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements IDeviceHandler, IDeviceDiscovered, IAD5932ConfigChanged {

    protected AD5932Fragment ad5932Fragment;

    protected ScanDeviceFragment scanDeviceFragment;
    protected BluetoothAdapter bluetoothAdapter;

    protected String deviceName;

    protected BluetoothGatt ad5932Gatt;
    protected BluetoothGattService ad5932configService;

    protected IIMUUpdate imuupdate;

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
            }else if(characteristic.getValue() != null && characteristic.getUuid().equals(ApplicationUUIDS.UUID_IMU_ACCEL)){
                Log.d("LOLOLOL", "" + characteristic.getValue());
                Log.d("LOLOLOL", "" + characteristic.getValue().length);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            printGattStatus(status);
            Log.d("Descriptor", "Wrote Destctiptor? " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //Log.d("Characteristic", "Changed");
            if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_IMU_ACCEL)){
                byte[] imu_data = characteristic.getValue();
                if (imuupdate != null && imu_data != null){
                    byte[] aXbytes = new byte[]{imu_data[0], imu_data[1], imu_data[2], imu_data[3]};
                    byte[] aYbytes = new byte[]{imu_data[4], imu_data[5], imu_data[6], imu_data[7]};
                    byte[] aZbytes = new byte[]{imu_data[8], imu_data[9], imu_data[10], imu_data[11]};
                    float aX = ByteBuffer.wrap(aXbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float aY = ByteBuffer.wrap(aYbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float aZ = ByteBuffer.wrap(aZbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    //Log.d("Acceleration", aX + ", " + aY + ", " + aZ);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            imuupdate.IMUAccelUpdate(aX, aY, aZ);
                        }
                    });
                }
            }else if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_IMU_GYRO)){
                byte[] imu_data = characteristic.getValue();
                if (imuupdate != null && imu_data != null){
                    byte[] gXbytes = new byte[]{imu_data[0], imu_data[1], imu_data[2], imu_data[3]};
                    byte[] gYbytes = new byte[]{imu_data[4], imu_data[5], imu_data[6], imu_data[7]};
                    byte[] gZbytes = new byte[]{imu_data[8], imu_data[9], imu_data[10], imu_data[11]};
                    float gX = ByteBuffer.wrap(gXbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float gY = ByteBuffer.wrap(gYbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float gZ = ByteBuffer.wrap(gZbytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    //Log.d("Gyroscope", gX + ", " + gY + ", " + gZ);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            imuupdate.IMUGyroUpdate(gX, gY, gZ);
                        }
                    });
                }
            }
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
        this.device = device;
        stopScanning();
        device.connectGatt(this, false, bluetoothGattCallback);
        Log.d("Bluetooth", "Connect to device");
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
            this.imuupdate = ad5932Fragment;
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
                    stopScanning();
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d("Bluetooth", "Started Scan");
            scanDeviceFragment.discoveryStarted();
        } else {
            stopScanning();
        }
    }

    private void stopScanning(){
        scanning = false;
        bluetoothLeScanner.stopScan(leScanCallback);
        Log.d("Bluetooth", "Stopped Scan");
        scanDeviceFragment.discoveryFinished();
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
        if (ad5932Gatt != null){
            ad5932Gatt.disconnect();
        }
        BluetoothDeviceContent.resetContent();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        scanDeviceFragment = new ScanDeviceFragment(this);
        transaction.replace(R.id.deviceViewer, scanDeviceFragment);
        transaction.commit();
        Log.d("Should switch", "Lol");
        scanDevice(200);
    }

    private void subscribeToBluetoothGATTCharacteristic(UUID service_uuid){
        BluetoothGattService imuService = ad5932Gatt.getService(service_uuid);
        if (imuService != null) {
            BluetoothGattCharacteristic imuCharacteristic = imuService.getCharacteristic(service_uuid);
            if (imuCharacteristic!= null) {
                BluetoothGattDescriptor imuDescriptor = imuCharacteristic.getDescriptor(ApplicationUUIDS.UUID_WOOODO_BLUETOOTHLE_SUBSCRIPTION_THING);
                if (imuDescriptor != null) {
                    Log.d("Setted up notification?", "?" + ad5932Gatt.setCharacteristicNotification(imuCharacteristic, true));
                    Log.d("SetValue", "?" + imuDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("Wrote Descriptor", "?" + ad5932Gatt.writeDescriptor(imuDescriptor));
                }else {
                    Log.e("BluetoothNotification", "Cannot allocate Descriptor");
                }
            }else {
                Log.e("BluetoothNotification", "Cannot allocate BluetoothNotification Characteristic");
            }
        }else {
            Log.e("BluetoothNotification", "Cannot allocate Service");
        }
    }

    public void readAD5932Config(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ad5932Gatt != null && ad5932configService != null){
                    ad5932Gatt.readCharacteristic(ad5932configService.getCharacteristic(ApplicationUUIDS.UUID_CONFIGURATION));
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ad5932Gatt.readCharacteristic(ad5932Gatt.getService(ApplicationUUIDS.UUID_IMU_ACCEL).getCharacteristic(ApplicationUUIDS.UUID_IMU_ACCEL));
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_IMU_ACCEL);
                    subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_IMU_GYRO);
                }
            }
        }, 200);
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