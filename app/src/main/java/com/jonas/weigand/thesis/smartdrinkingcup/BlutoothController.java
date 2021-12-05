package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BlutoothController extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch(state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth turned off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth gets shutdowned");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth turned on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(String.valueOf(R.string.bluetooth_debug), "Bluetooth starting");
                    break;
            }
        }
        if(action.equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String deviceHardwareAddress = device.getAddress();
            //TODO Add device to Device List
        }
    }
}
