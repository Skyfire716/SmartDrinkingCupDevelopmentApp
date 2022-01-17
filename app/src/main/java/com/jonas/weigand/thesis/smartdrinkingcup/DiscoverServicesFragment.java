package com.jonas.weigand.thesis.smartdrinkingcup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverServicesFragment extends Fragment implements  IDiscoverEvent, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String SCANSTARTEDSTR = "Scan Started...";
    private final String SERVICESDISCOVEREDSTR = "Services Discovered";
    public static final String CAPACITIVEDEVICE = "Capacitive Cup";
    public static final String LASERDEVICE = "Laser Cup";
    public static final String ULTRASONICDEVICE = "Ultrasonic Cup";
    public static final String NOSMARTDEVICE = "This is not a SmartDrinking Cup";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private IDeviceHandler deviceHandler;

    private TextView connect;
    private TextView scan;
    private TextView detected;
    private TextView devicefound;
    private Button backToMenuBtn;
    private ProgressBar serviceScanBar;

    private UUIDItemFragment uuidItemFragment;

    public DiscoverServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverServicesFragment newInstance(String param1, String param2) {
        DiscoverServicesFragment fragment = new DiscoverServicesFragment();
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

    public void setDeviceHandler(IDeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connect = (TextView) view.findViewById(R.id.connectingToText);
        scan = (TextView) view.findViewById(R.id.scanningForServicesText);
        detected = (TextView) view.findViewById(R.id.detectedServicesText);
        devicefound = (TextView)view.findViewById(R.id.foundDrinkingCupText);
        connect.setTextColor(getResources().getColor(R.color.gray));
        connect.setText("Device");
        scan.setTextColor(getResources().getColor(R.color.gray));
        scan.setText(SCANSTARTEDSTR);
        detected.setTextColor(getResources().getColor(R.color.gray));
        detected.setText(SERVICESDISCOVEREDSTR);
        devicefound.setTextColor(getResources().getColor(R.color.gray));
        devicefound.setText("Check Device");
        backToMenuBtn = (Button) view.findViewById(R.id.backtoMainBtn);
        backToMenuBtn.setOnClickListener(this);
        serviceScanBar = (ProgressBar) view.findViewById(R.id.serviceScanProgressBar);
        AD5932Fragment.collapseView(serviceScanBar);
        AD5932Fragment.collapseView(backToMenuBtn);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        uuidItemFragment = new UUIDItemFragment();
        transaction.replace(R.id.serviceUUIDContainer, uuidItemFragment);
        transaction.commit();
    }

    @Override
    public void communicationUpdate(CommunicationEnum type, String text) {
        switch (type) {
            case DEVICENAME:
                connect.setTextColor(getResources().getColor(R.color.green));
                connect.setText(text);
                break;
            case SCANSTARTED:
                if (text.contains("failed")){
                    scan.setTextColor(getResources().getColor(R.color.red));
                    scan.setText("Something went wrong");
                }else {
                    AD5932Fragment.expand(serviceScanBar);
                    scan.setTextColor(getResources().getColor(R.color.green));
                    scan.setText(SCANSTARTEDSTR);
                }
                break;
            case SERVICESDETECTED:
                AD5932Fragment.collapseView(serviceScanBar);
                detected.setTextColor(getResources().getColor(R.color.green));
                detected.setText(SERVICESDISCOVEREDSTR);
                UUIDItemContent.addItem(new UUIDItemContent.UUIDItem(text));
                if (uuidItemFragment != null) {
                    uuidItemFragment.updateView();
                }
                break;
            case SMARTDRINKINGCUPDISCOVERED:
                Log.d("DiscoverServicesFragment", "Connected To " + text);
                if ((ULTRASONICDEVICE + LASERDEVICE + CAPACITIVEDEVICE).contains(text)) {
                    devicefound.setText(text);
                    devicefound.setTextColor(getResources().getColor(R.color.green));
                } else {
                    Log.e("DiscoverServicesFragment", "No Smart Device " + text);
                    AD5932Fragment.expand(backToMenuBtn);
                    devicefound.setText(NOSMARTDEVICE);
                    devicefound.setTextColor(getResources().getColor(R.color.red));
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == backToMenuBtn){
            if (deviceHandler != null){
                deviceHandler.disconnectedToDevice();
            }
        }
    }
}