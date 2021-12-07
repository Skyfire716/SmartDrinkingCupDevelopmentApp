package com.jonas.weigand.thesis.smartdrinkingcup;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanDeviceFragment extends Fragment implements View.OnClickListener, IDeviceDiscovered{

    protected Button refreshDevicesBtn;
    protected BluetoothDevicesFragment devicesViewer;
    protected TextView scanStatusText;
    protected ProgressBar scanProgressSpinner;
    protected IDeviceHandler deviceHandler;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanDeviceFragment() {
        // Required empty public constructor
    }

    public ScanDeviceFragment(IDeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    public BluetoothDevicesFragment getDevicesViewer() {
        return devicesViewer;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanDeviceFragment newInstance(String param1, String param2) {
        ScanDeviceFragment fragment = new ScanDeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(deviceHandler == null){
            Log.e("ERROR", "DeviceHandler is null");
        }
        devicesViewer = new BluetoothDevicesFragment(deviceHandler);
        transaction.replace(R.id.devicesContainer, devicesViewer);
        transaction.commit();
        scanStatusText = (TextView) getView().findViewById(R.id.scanStateText);
        scanStatusText.setText(getResources().getString(R.string.scanner_scanning));
        scanProgressSpinner = (ProgressBar) getView().findViewById(R.id.scanProgress);
        refreshDevicesBtn = (Button) getView().findViewById(R.id.refreshButton);
        refreshDevicesBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == refreshDevicesBtn){
            deviceHandler.scanDevice(10);
        }
    }

    @Override
    public void discoveredDevice(BluetoothDevice device) {
        Log.d(getResources().getString(R.string.bluetooth_debug), "Discovered Device " + device.getName());
        BluetoothDeviceContent.addItem(new BluetoothDeviceItem(device, device.getName() == null ? getResources().getString(R.string.no_name_device) : device.getName(), device.getAddress()));
        devicesViewer.updateDevies();
    }

    @Override
    public void discoveryStarted() {
        if(scanStatusText != null && scanProgressSpinner != null) {
            scanStatusText.setText(R.string.scanner_scanning);
            scanProgressSpinner.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void discoveryFinished() {
        if(scanStatusText != null && scanProgressSpinner != null) {
            scanStatusText.setText(R.string.scanner_done);
            scanProgressSpinner.setVisibility(View.INVISIBLE);
        }
    }
}