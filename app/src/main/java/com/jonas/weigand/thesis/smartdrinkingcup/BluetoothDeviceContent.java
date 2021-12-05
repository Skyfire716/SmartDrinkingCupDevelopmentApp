package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BluetoothDeviceContent {

    public static ArrayList<BluetoothDeviceItem> items = new ArrayList<>();

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
