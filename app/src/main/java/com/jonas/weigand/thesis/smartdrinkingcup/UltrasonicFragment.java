package com.jonas.weigand.thesis.smartdrinkingcup;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.internal.TextWatcherAdapter;

import org.w3c.dom.Text;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UltrasonicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UltrasonicFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, IUltrasonicDataUpdate {

    private Button toggleServoBtn;
    private TextView servoStateView;
    private TextView intervalText;
    private TextView distanceText;
    private EditText minServoAngle;
    private EditText maxServoAngle;
    private SeekBar updateInterval;
    private SeekBar currentServoAngle;
    private DataCollector dataCollector;

    private IUltrasonicDataUpdate iUltrasonicDataUpdate;
    private IUltrasonicConfigChanged iUltrasonicConfigChanged;
    private IDeviceHandler deviceHandler;
    private int minServoAngleInt;
    private int maxServoAngleInt;
    private byte data[] = {0x00, 0x14, 0x00, (byte)0xB4};

    private TextWatcher minTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                return;
            }
            int value = 0;
            if (s != null) {
                try {
                    value = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + s.toString() + "\" could not be parsed as a number")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    e.printStackTrace();
                }
                if (value < 0 || value > 180) {
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + s.toString() + "\" must be >= 0 or <= 180")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                } else {
                    minServoAngleInt = value;
                    //TODO update Progressbar Range
                    data[2] = (byte) minServoAngleInt;
                    if (iUltrasonicConfigChanged != null) {
                        iUltrasonicConfigChanged.ultrasonicConfigChanged(data);
                    }
                }
            }
        }
    };
    private TextWatcher maxTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                return;
            }
            int value = 0;
            if (s != null) {
                try {
                    value = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + s.toString() + "\" could not be parsed as a number")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    e.printStackTrace();
                }
                if (value < 0 || value > 180) {
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + s.toString() + "\" must be >= 0 or <= 180")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                } else {
                    maxServoAngleInt = value;
                    //TODO update Progressbar Range
                    data[3] = (byte) maxServoAngleInt;
                    if (iUltrasonicConfigChanged != null) {
                        iUltrasonicConfigChanged.ultrasonicConfigChanged(data);
                    }
                }
            }
        }
    };


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UltrasonicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UltrasonicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UltrasonicFragment newInstance(String param1, String param2) {
        UltrasonicFragment fragment = new UltrasonicFragment();
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

    public void setMainActivity(MainActivity mainActivity){
        if (dataCollector != null){
            dataCollector.setMainActivity(mainActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ultrasonic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleServoBtn = (Button) view.findViewById(R.id.toggleServoBtn);
        toggleServoBtn.setOnClickListener(this);
        servoStateView = (TextView) view.findViewById(R.id.servoStateText);
        distanceText = (TextView) view.findViewById(R.id.distanceText);
        intervalText = (TextView) view.findViewById(R.id.intervalText);
        intervalText.setText("20ms");
        minServoAngle = (EditText) view.findViewById(R.id.minServoValEditText);
        minServoAngle.addTextChangedListener(minTextWatcher);
        maxServoAngle = (EditText) view.findViewById(R.id.maxServoValEditText);
        maxServoAngle.addTextChangedListener(maxTextWatcher);
        updateInterval = (SeekBar) view.findViewById(R.id.updateIntervalBar);
        updateInterval.setOnSeekBarChangeListener(this);
        currentServoAngle = (SeekBar) view.findViewById(R.id.servoPosSeekBar);
        currentServoAngle.setOnSeekBarChangeListener(this);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        dataCollector = new DataCollector();
        transaction.replace(R.id.dataCollectorContainer, dataCollector);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v == toggleServoBtn) {
            //TODO sent command to Arduino to move Servo
            data[1] = (byte) 0x80;
            if (iUltrasonicConfigChanged != null) {
                iUltrasonicConfigChanged.ultrasonicConfigChanged(data);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == updateInterval) {
            //TODO inform Arduino of updateInterval Change
            //Min Interval Time 20ms
            //Max transferable Bytes 7 leading to max transferable Time
            int interval = 20 + (int) (Math.pow(2, 7) * (progress / 100.0));
            intervalText.setText(interval + "ms");
            data[1] = (byte) (interval & 0x7F);
            if (iUltrasonicConfigChanged != null) {
                iUltrasonicConfigChanged.ultrasonicConfigChanged(data);
            }
        } else if (seekBar == currentServoAngle) {
            //TODO inform Servo of new Position
            int servoAngle = minServoAngleInt > maxServoAngleInt ? maxServoAngleInt + (int) ((minServoAngleInt - maxServoAngleInt) * (progress / 100.0)) : minServoAngleInt + (int) ((maxServoAngleInt - minServoAngleInt) * (progress / 100.0));
            data[0] = (byte) servoAngle;
            if (iUltrasonicConfigChanged != null) {
                iUltrasonicConfigChanged.ultrasonicConfigChanged(data);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setDeviceHandler(IDeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    public void setIUltrasonicConfigChanged(IUltrasonicConfigChanged iUltrasonicConfigChanged) {
        this.iUltrasonicConfigChanged = iUltrasonicConfigChanged;
    }

    @Override
    public void distanceUpdate(float distance) {
        if (distanceText!= null){
            float minMlDistance = 15.9f;
            float maxMlDistance = 5.1f;
            float maxMl = 309;
            distance = (float) (Math.round(distance * 10.0) / 10.0);
            distanceText.setText(distance + "cm\n" +
                    (distance - minMlDistance) * (maxMl) / (maxMlDistance - minMlDistance) + "ml");
        }
    }
}