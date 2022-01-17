package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

public interface IDeviceHandler {

    void connectToDevice(BluetoothDevice device);
    void connectedToDevice();
    void disconnectedToDevice();
    void scanDevice(int delay, boolean filter);
}
