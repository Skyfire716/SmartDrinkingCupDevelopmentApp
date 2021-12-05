package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

public interface IDeviceHandler {

    public void connectToDevice(BluetoothDevice device);
}
