package com.jonas.weigand.thesis.smartdrinkingcup;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UltrasonicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UltrasonicFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button toggleServoBtn;
    private TextView servoStateView;
    private EditText minServoAngle;
    private EditText maxServoAngle;
    private SeekBar updateInterval;
    private SeekBar currentServoAngle;

    private int minServoAngleInt;
    private int maxServoAngleInt;

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
                    minServoAngle.setText(s);
                    minServoAngleInt = value;
                    //TODO update Progressbar Range
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
                    maxServoAngle.setText(s);
                    maxServoAngleInt = value;
                    //TODO update Progressbar Range
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
        minServoAngle = (EditText) view.findViewById(R.id.minServoValEditText);
        minServoAngle.addTextChangedListener(minTextWatcher);
        maxServoAngle = (EditText) view.findViewById(R.id.maxServoValEditText);
        maxServoAngle.addTextChangedListener(maxTextWatcher);
        updateInterval = (SeekBar) view.findViewById(R.id.updateIntervalBar);
        updateInterval.setOnSeekBarChangeListener(this);
        currentServoAngle = (SeekBar) view.findViewById(R.id.servoPosSeekBar);
        currentServoAngle.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == toggleServoBtn){
            //TODO sent command to Arduino to move Servo
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == updateInterval){
            //TODO inform Arduino of updateInterval Change
        }else if (seekBar == currentServoAngle){
            //TODO inform Servo of new Position
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}