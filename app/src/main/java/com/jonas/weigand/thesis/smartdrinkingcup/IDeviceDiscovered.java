package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

public interface IDeviceDiscovered {

    public void discoveredDevice(BluetoothDevice device);
    public void discoveryStarted();
    public void discoveryFinished();
}
