package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class BluetoothDeviceItem {

    public final BluetoothDevice device;
    public final String deviceName;
    public final String deviceMAC;

    public BluetoothDeviceItem(BluetoothDevice device, String deviceName, String deviceMAC){
        this.device = device;
        this.deviceName = deviceName;
        this.deviceMAC = deviceMAC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothDeviceItem that = (BluetoothDeviceItem) o;
        return Objects.equals(deviceMAC, that.deviceMAC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceMAC);
    }

    @Override
    public String toString() {
        return "BluetoothDeviceItem{" +
                "device='" + device + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceMAC='" + deviceMAC + '\'' +
                '}';
    }
}
