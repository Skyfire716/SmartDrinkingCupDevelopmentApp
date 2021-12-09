package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BluetoothDeviceContent {

    public static HashSet<BluetoothDeviceItem> items = new HashSet<>();

    public static Map<BluetoothDevice, BluetoothDeviceItem> itemMap = new HashMap<>();

    public static void addItem(BluetoothDeviceItem item){
        if (!items.contains(item)) {
            items.add(item);
            itemMap.put(item.device, item);
        }
    }

    public static void resetContent(){
        itemMap.clear();
        items.clear();
    }
}
