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
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements IDeviceHandler, IDeviceDiscovered, IAD5932ConfigChanged, IUltrasonicConfigChanged {

    protected AD5932Fragment ad5932Fragment;
    protected UltrasonicFragment ultrasonicFragment;
    protected ScanDeviceFragment scanDeviceFragment;
    protected DiscoverServicesFragment discoverServicesFragment;
    protected BluetoothAdapter bluetoothAdapter;

    protected String deviceName;

    protected BluetoothGatt ad5932Gatt;
    protected BluetoothGattService ad5932configService;

    protected IIMUUpdate imuupdate;
    protected IUltrasonicDataUpdate iUltrasonicDataUpdate;
    protected IDiscoverEvent discoverEventListener;
    protected IAD5932DataUpdate iad5932DataUpdate;

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
                communicate(CommunicationEnum.DEVICENAME, "Connecting to: " + gatt.getDevice().getName() + " " + gatt.getDevice().getAddress());
                if(gatt.discoverServices()){
                    communicate(CommunicationEnum.SCANSTARTED, "");
                }else{
                    communicate(CommunicationEnum.SCANSTARTED, "failed");
                }
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
            if (characteristic.getValue() != null) {
                if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_CONFIGURATION)) {
                    for (int i = 0; i < characteristic.getValue().length; i += 2) {
                        short a = (short) (((((short) characteristic.getValue()[i + 1]) << 8) & 0xFF00) | (((short) characteristic.getValue()[i]) & 0xFF));
                        Log.d("Byte", "" + Integer.toBinaryString((a & 0xFFFF)));
                    }
                    AD5932Config ad5932Config = new AD5932Config();
                    Log.d("Can Load From Data?", "" + ad5932Config.loadConfigFromTransfer(characteristic.getValue()));
                    Log.d("AD5932Config", ad5932Config.toString());
                    ad5932Fragment.setConfig(ad5932Config);
                } else if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_MEASURING_TYPE)) {
                    if (discoverEventListener != null){
                        String deviceCapability = new String(characteristic.getValue(), StandardCharsets.US_ASCII);
                        communicate(CommunicationEnum.SMARTDRINKINGCUPDISCOVERED, deviceCapability);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switchToSmartCupFragment(deviceCapability);
                            }
                        }, 750);
                        if ((DiscoverServicesFragment.ULTRASONICDEVICE + DiscoverServicesFragment.LASERDEVICE + DiscoverServicesFragment.CAPACITIVEDEVICE).contains(deviceCapability)){
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    subscribeToIMU();
                                }
                            }, 250);
                            if (DiscoverServicesFragment.ULTRASONICDEVICE.contains(deviceCapability)){
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscribeToUltrasonicDistance();
                                    }
                                }, 550);
                            }
                            if (DiscoverServicesFragment.CAPACITIVEDEVICE.contains(deviceCapability)){
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        subscribeToAD5932DataUpdate();
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        subscribeToAD5932EOS();
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        readAD5932Config();
                                        Log.d("Main", "Read Config");
                                    }
                                }, 500);
                            }
                        }
                    }
                }
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
            }else if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_HCSR04_DISTANCE)){
                byte[] distance_data = characteristic.getValue();
                if (distance_data != null && iUltrasonicDataUpdate != null){
                    float distance = ByteBuffer.wrap(distance_data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            iUltrasonicDataUpdate.distanceUpdate(distance);
                        }
                    });
                }
            }else if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_AD5932_MEASURE_RESULT)){
                byte[] transferedData = characteristic.getValue();
                Log.d("Main", "Measured Data Update " + transferedData.length + " " + iad5932DataUpdate);
                if (transferedData != null && transferedData.length > 7 && iad5932DataUpdate != null) {
                    byte[] frequencyBytes = new byte[]{transferedData[0], transferedData[1], transferedData[2], transferedData[3]};
                    byte[] responseBytes = new byte[]{transferedData[4], transferedData[5], transferedData[6], transferedData[7]};
                    float freq = ByteBuffer.wrap(frequencyBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float resp = ByteBuffer.wrap(responseBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Main", "Pass Data Update Through");
                            iad5932DataUpdate.dataUpdated(freq, resp);
                        }
                    });
                }
            }else if (characteristic.getUuid().equals(ApplicationUUIDS.UUID_TRIGGER)){
                byte[] trans = characteristic.getValue();
                if (trans != null && iad5932DataUpdate != null){
                    if ((trans[0] & 0x04) == 0x04){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                iad5932DataUpdate.EndOfScan();
                            }
                        });
                    }
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
                ArrayList<BluetoothGattService> services = new ArrayList<>(gatt.getServices());
                if (services.isEmpty()) {
                    Log.d("Bluetooth", "Discovered empty service");
                }
                ad5932Gatt = gatt;
                for (BluetoothGattService service : services) {
                    communicate(CommunicationEnum.SERVICESDETECTED, service.getUuid().toString());
                    if (service.getUuid().equals(ApplicationUUIDS.UUID_MEASURING_TYPE)){
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gatt.readCharacteristic(service.getCharacteristic(ApplicationUUIDS.UUID_MEASURING_TYPE));
                            }
                        }, 500);
                    }
                    Log.d("Bluetooth", "Found Service " + service.getUuid());
                    if (service.getUuid().equals(ApplicationUUIDS.UUID_CONFIGURATION)) {
                        ad5932Gatt = gatt;
                        ad5932configService = service;
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //readAD5932Config();
                            }
                        }, 1000);
                    }
                }
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
        scanDevice(750, false);
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
        discoverServicesFragment = new DiscoverServicesFragment();
        discoverEventListener = discoverServicesFragment;
        transaction.replace(R.id.deviceViewer, discoverServicesFragment);
        transaction.commit();
        /*
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
        */
    }

    @Override
    public void disconnectedToDevice() {
        ad5932Fragment = null;
        ultrasonicFragment = null;
        scanDeviceFragment = null;
        discoverServicesFragment = null;
        ad5932Gatt = null;
        ad5932configService = null;

        imuupdate = null;
        iUltrasonicDataUpdate = null;
        discoverEventListener = null;
        iad5932DataUpdate = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        scanDeviceFragment = new ScanDeviceFragment(this);
        transaction.replace(R.id.deviceViewer, scanDeviceFragment);
        transaction.commit();
    }

    @Override
    public void scanDevice(int delay, boolean filter) {
        BluetoothDeviceContent.resetContent();
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLeDevice(filter);
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

    private void scanLeDevice(boolean filter) {
        if (!scanning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, SCAN_PERIOD);
            scanning = true;
            if (filter) {
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
                ArrayList<ScanFilter> filters = new ArrayList<>();
                ScanFilter filter1 = new ScanFilter.Builder().setDeviceName("AD5932 Device").build();
                ScanFilter filter2 = new ScanFilter.Builder().setDeviceAddress("84:CC:A8:30:1E:62").build();
                ScanFilter uuidFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(ApplicationUUIDS.UUID_MEASURING_TYPE), new ParcelUuid(ApplicationUUIDS.UUID_DEFAULT_MASK)).build();
                filters.add(uuidFilter);
                //filters.add(filter);
                //filters.add(filter2);
                //84:CC:A8:30:1E:62
                bluetoothLeScanner.startScan(filters, settings, leScanCallback);
            }else {
                bluetoothLeScanner.startScan(leScanCallback);
            }
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
        scanDevice(200, false);
    }

    private void subscribeToBluetoothGATTCharacteristic(UUID service_uuid){
        BluetoothGattService notificationService = ad5932Gatt.getService(service_uuid);
        if (notificationService != null) {
            BluetoothGattCharacteristic notificationServiceCharacteristic = notificationService.getCharacteristic(service_uuid);
            if (notificationServiceCharacteristic!= null) {
                BluetoothGattDescriptor notificationServiceCharacteristicDescriptor = notificationServiceCharacteristic.getDescriptor(ApplicationUUIDS.UUID_WOOODO_BLUETOOTHLE_SUBSCRIPTION_THING);
                if (notificationServiceCharacteristicDescriptor != null) {
                    Log.d("Setted up notification?", "?" + ad5932Gatt.setCharacteristicNotification(notificationServiceCharacteristic, true));
                    Log.d("SetValue", "?" + notificationServiceCharacteristicDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("Wrote Descriptor", "?" + ad5932Gatt.writeDescriptor(notificationServiceCharacteristicDescriptor));
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

    public void subscribeToIMU(){
        subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_IMU_ACCEL);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_IMU_GYRO);
    }

    public void subscribeToUltrasonicDistance(){
        subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_HCSR04_DISTANCE);
    }

    private void subscribeToAD5932DataUpdate() {
        subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_AD5932_MEASURE_RESULT);
    }

    private void subscribeToAD5932EOS(){
        subscribeToBluetoothGATTCharacteristic(ApplicationUUIDS.UUID_TRIGGER);
    }

    public void readAD5932Config(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ad5932Gatt != null && ad5932configService != null){
                    ad5932Gatt.readCharacteristic(ad5932configService.getCharacteristic(ApplicationUUIDS.UUID_CONFIGURATION));
                    /*
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ad5932Gatt.readCharacteristic(ad5932Gatt.getService(ApplicationUUIDS.UUID_IMU_ACCEL).getCharacteristic(ApplicationUUIDS.UUID_IMU_ACCEL));
                     */
                }
            }
        }, 200);
    }

    private void communicate(CommunicationEnum type, String text){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (discoverEventListener != null){
                    discoverEventListener.communicationUpdate(type, text);
                }
            }
        }, 550);
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

    @Override
    public void trigger() {
        if (ad5932Gatt != null){
            Log.d("Bluetooth", "Trigger");
            BluetoothGattCharacteristic triggerCharacteristic = ad5932Gatt.getService(ApplicationUUIDS.UUID_TRIGGER).getCharacteristic(ApplicationUUIDS.UUID_TRIGGER);
            triggerCharacteristic.setValue(new byte[]{0x01});
            ad5932Gatt.writeCharacteristic(triggerCharacteristic);
        }
    }

    private void switchToSmartCupFragment(String deviceCapability){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (deviceCapability.contains(DiscoverServicesFragment.CAPACITIVEDEVICE)){
            ad5932Fragment = new AD5932Fragment();
            transaction.replace(R.id.deviceViewer, ad5932Fragment);
            transaction.commit();
            if (ad5932Fragment == null) {
                Log.d("ADFragment", "Is null");
            } else {
                ad5932Fragment.setAD5932ConfigChanged(this);
                ad5932Fragment.setDeviceText(deviceName);
                this.imuupdate = ad5932Fragment;
                this.iad5932DataUpdate = ad5932Fragment;
            }
        }else if (deviceCapability.contains(DiscoverServicesFragment.LASERDEVICE)){

        }else if(deviceCapability.contains(DiscoverServicesFragment.ULTRASONICDEVICE)){
            ultrasonicFragment = new UltrasonicFragment();
            transaction.replace(R.id.deviceViewer, ultrasonicFragment);
            transaction.commit();
            if (ultrasonicFragment == null) {
                Log.d("ADFragment", "Is null");
            } else {
                ultrasonicFragment.setDeviceHandler(this);
                iUltrasonicDataUpdate = ultrasonicFragment;
                ultrasonicFragment.setIUltrasonicConfigChanged(this);
            }
        }
    }

    @Override
    public void reset() {
        if (ad5932Gatt != null){
            Log.d("Bluetooth", "Reset");
            BluetoothGattCharacteristic triggerCharacteristic = ad5932Gatt.getService(ApplicationUUIDS.UUID_TRIGGER).getCharacteristic(ApplicationUUIDS.UUID_TRIGGER);
            triggerCharacteristic.setValue(new byte[]{0x02});
            ad5932Gatt.writeCharacteristic(triggerCharacteristic);
        }
    }

    @Override
    public void ultrasonicConfigChanged(byte[] data) {
        if (ad5932Gatt != null) {
            Log.d("Bluetooth", "Update Ultrasonic");
            Log.d("UltrasonicData", "" + Integer.toBinaryString(data[0]));
            Log.d("UltrasonicData", "" + Integer.toBinaryString(data[1]));
            Log.d("UltrasonicData", "" + Integer.toBinaryString(data[2]));
            Log.d("UltrasonicData", "" + Integer.toBinaryString(data[3]));
            BluetoothGattCharacteristic ultrasonicCharacteristic = ad5932Gatt.getService(ApplicationUUIDS.UUID_HCSR04_CONTROL).getCharacteristic(ApplicationUUIDS.UUID_HCSR04_CONTROL);
            ultrasonicCharacteristic.setValue(data);
            ad5932Gatt.writeCharacteristic(ultrasonicCharacteristic);
        }else{
            Log.d("Bluetooth", "Gatt is null");
        }
    }
}