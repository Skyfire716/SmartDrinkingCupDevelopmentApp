package com.jonas.weigand.thesis.smartdrinkingcup;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LaserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LaserFragment extends Fragment implements ILaserUpdate {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView distanceResult;
    private TextView intervalText;
    private DataCollector dataCollector;
    private SeekBar intervalSeekbar;


    protected GraphView laserCurve;
    protected LineGraphSeries<DataPoint> laserSeries;
    protected LineGraphSeries<DataPoint> voltageSeries;
    private int p = 0;

    public LaserFragment() {
        // Required empty public constructor
    }


    public void setMainActivity(MainActivity mainActivity) {
        if (dataCollector != null) {
            this.dataCollector.setMainActivity(mainActivity);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LaserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LaserFragment newInstance(String param1, String param2) {
        LaserFragment fragment = new LaserFragment();
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
        return inflater.inflate(R.layout.fragment_laser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.distanceResult = (TextView) view.findViewById(R.id.laserDistanceText);
        this.intervalText = (TextView) view.findViewById(R.id.laserIntervalText);
        this.intervalSeekbar = (SeekBar) view.findViewById(R.id.laserIntervalSeekbar);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        dataCollector = new DataCollector();
        transaction.replace(R.id.laserCollectContainer, dataCollector);
        transaction.commit();

        laserCurve = (GraphView) getView().findViewById(R.id.graphFragment);
        laserCurve.setTitle("Sweep Response");
        // activate horizontal zooming and scrolling
        laserCurve.getViewport().setScalable(true);
        // activate horizontal scrolling
        laserCurve.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        laserCurve.getViewport().setScalableY(true);
        // activate vertical scrolling
        laserCurve.getViewport().setScrollableY(true);

        laserCurve.getGridLabelRenderer().setHorizontalAxisTitle("time");
        laserCurve.getGridLabelRenderer().setVerticalAxisTitle("Amplitude V");
        if (laserSeries == null){
            laserSeries = new LineGraphSeries<>();
            laserSeries.setTitle("Laser Results");
        }
        if (voltageSeries == null){
            voltageSeries = new LineGraphSeries<>();
            voltageSeries.setTitle("Voltage");
            voltageSeries.setColor(Color.GREEN);
        }
        laserCurve.addSeries(laserSeries);
        laserCurve.addSeries(voltageSeries);
        laserCurve.getLegendRenderer().setVisible(true);
        laserCurve.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    @Override
    public void updateDistance(float distance, float voltage) {
        this.distanceResult.setText(distance + "cm");
        laserSeries.appendData(new DataPoint(p, distance), true, 200, false);
        voltageSeries.appendData(new DataPoint(p, voltage), true, 200, false);
        p++;
    }

    @Override
    public void gotInterval(byte interval) {
        this.intervalText.setText(interval + "ms");
    }
}