package com.jonas.weigand.thesis.smartdrinkingcup;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataCollector#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataCollector extends Fragment  implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    protected Button start_stop_btn;
    protected Spinner glass_spinner;
    protected Spinner beverage_spinner;
    private MainActivity mainActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String glass;
    private String beverage;

    public DataCollector() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataCollector.
     */
    // TODO: Rename and change types and number of parameters
    public static DataCollector newInstance(String param1, String param2) {
        DataCollector fragment = new DataCollector();
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

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_collector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.beverage_spinner = (Spinner) view.findViewById(R.id.beverageSpinner);
        this.glass_spinner = (Spinner) view.findViewById(R.id.glassSpinner);
        this.start_stop_btn = (Button) view.findViewById(R.id.dataCollectBtn);
        this.start_stop_btn.setOnClickListener(this);
        this.beverage_spinner.setOnItemSelectedListener(this);
        this.glass_spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == start_stop_btn){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mainActivity.collectDataAbout(glass, beverage);
            }
            if (start_stop_btn.getText().equals("Stop Data Collection")){
                start_stop_btn.setText("Start Data Collection");
            }else {
                start_stop_btn.setText("Stop Data Collection");
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == beverage_spinner){
            beverage = (String) adapterView.getItemAtPosition(i);
            Log.d("DataCollector", "Selected Beverage " + beverage);
        }else  if (adapterView == glass_spinner){
            glass = (String) adapterView.getItemAtPosition(i);
            Log.d("DataCollector", "Selected glass " + glass);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}