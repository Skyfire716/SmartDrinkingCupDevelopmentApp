package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothClientManager extends Thread{

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean running;

    public BluetoothClientManager(BluetoothDevice device){
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(ApplicationUUIDS.UUID_WAVEFORM);
        }catch (IOException e){
            Log.e("bluetooth_debug", "Socket creation failed", e);
        }
        mmSocket = tmp;
        running = true;
    }

    @Override
    public void run() {
        super.run();
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("bluetooth_debug", "Could not close the client socket", closeException);
            }
            return;
        }
        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);
        try {
            inputStream = mmSocket.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            outputStream = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream != null && outputStream != null){
            while (running){

            }
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        running = false;
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("bluetooth_debug", "Could not close the client socket", e);
        }
    }

}
