package com.jonas.weigand.thesis.smartdrinkingcup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AD5932Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AD5932Fragment extends Fragment implements View.OnClickListener, IIMUUpdate, IAD5932DataUpdate {

    private String deviceName;
    protected TextView deviceNameTextView;

    private DecimalFormat df = new DecimalFormat("0.00");

    protected TextView ax;
    protected TextView ay;
    protected TextView az;
    protected TextView gx;
    protected TextView gy;
    protected TextView gz;

    protected boolean settings_visible = true;

    protected Button backBtn;
    protected Button collapseSettings;
    protected Button triggerBtn;
    protected Button resetBtn;
    protected AD5932SettingsFragment ad5932SettingsFragment;
    protected FragmentContainerView fragmentContainerView;
    protected FragmentContainerView cupRendererContainer;

    protected DataCollector dataCollector;

    protected GraphView frequencyCurve;
    protected LineGraphSeries<DataPoint> frequencySeries;
    protected ArrayList<DataPoint> dataForSeries = new ArrayList<>(200);

    private IAD5932ConfigChanged IAD5932ConfigChanged;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public void setMainActivity(MainActivity mainActivity){
        if (dataCollector!=null){
            dataCollector.setMainActivity(mainActivity);
        }
    }
    public AD5932Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AD5932Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AD5932Fragment newInstance(String param1, String param2) {
        AD5932Fragment fragment = new AD5932Fragment();
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
        return inflater.inflate(R.layout.fragment_a_d5932, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentContainerView = (FragmentContainerView)  getView().findViewById(R.id.ad5932settings);
        cupRendererContainer = (FragmentContainerView) getView().findViewById(R.id.cupRendererContainer);
        collapseSettings = (Button) getView().findViewById(R.id.collapseSettings);
        backBtn = (Button) getView().findViewById(R.id.returnButton);
        backBtn.setOnClickListener(this);
        deviceNameTextView = (TextView) getView().findViewById(R.id.deviceNameTextView);
        deviceNameTextView.setText(deviceName);
        collapseSettings.setOnClickListener(this);
        triggerBtn = (Button) getView().findViewById(R.id.triggerBtn);
        triggerBtn.setOnClickListener(this);

        frequencyCurve = (GraphView) getView().findViewById(R.id.frequencyCurveGraph);
        frequencyCurve.setTitle("Sweep Response");
        // activate horizontal zooming and scrolling
        frequencyCurve.getViewport().setScalable(true);
        // activate horizontal scrolling
        frequencyCurve.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        frequencyCurve.getViewport().setScalableY(true);
        // activate vertical scrolling
        frequencyCurve.getViewport().setScrollableY(true);
        frequencyCurve.getViewport().setMaxX(3500000);
        frequencyCurve.getViewport().setMinX(1000);

        frequencyCurve.getGridLabelRenderer().setHorizontalAxisTitle("f in Hz");
        frequencyCurve.getGridLabelRenderer().setVerticalAxisTitle("Amplitude");
        if (frequencySeries == null){
            frequencySeries = new LineGraphSeries<>();
            frequencySeries.setTitle("Capacitive Characteristic");
        }
        frequencyCurve.addSeries(frequencySeries);
        frequencyCurve.getLegendRenderer().setVisible(true);
        frequencyCurve.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        resetBtn = (Button) getView().findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        ax = (TextView) getView().findViewById(R.id.ax);
        ay = (TextView) getView().findViewById(R.id.ay);
        az = (TextView) getView().findViewById(R.id.az);
        gx = (TextView) getView().findViewById(R.id.gx);
        gy = (TextView) getView().findViewById(R.id.gy);
        gz = (TextView) getView().findViewById(R.id.gz);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ad5932SettingsFragment = new AD5932SettingsFragment();
        transaction.replace(R.id.ad5932settings, ad5932SettingsFragment);
        transaction.commit();
        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
        CupFragment cupFragment = new CupFragment();
        transaction1.replace(R.id.cupRendererContainer, cupFragment);
        transaction1.addToBackStack(null);
        transaction1.commit();
        ad5932SettingsFragment.setIAD5932ConfigChanged(IAD5932ConfigChanged);
        FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
        dataCollector = new DataCollector();
        transaction2.replace(R.id.addataCollectorContainers, dataCollector);
        transaction2.commit();
     }

    @Override
    public void onClick(View v) {
        if (v== backBtn){
            IAD5932ConfigChanged.returnToScanDevice();
        }
        if(v == collapseSettings){
            if (settings_visible){
                collapseView(ad5932SettingsFragment.getView());
                collapseSettings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_keyboard_arrow_right_24, 0, 0, 0);
            }else {
                expand(ad5932SettingsFragment.getView());
                collapseSettings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_keyboard_arrow_down_24, 0, 0, 0);
            }
            settings_visible = !settings_visible;
        }
        if (v == triggerBtn){
            IAD5932ConfigChanged.trigger();
        }
        if (v == resetBtn){
            IAD5932ConfigChanged.reset();
        }
    }

    //https://stackoverflow.com/a/13381228

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ConstraintLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseView(final View v){
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void setAD5932ConfigChanged(IAD5932ConfigChanged configChanged) {
        this.IAD5932ConfigChanged = configChanged;
    }

    public void setDeviceText(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setConfig(AD5932Config ad5932Config) {
        ad5932SettingsFragment.setAD5932Config(ad5932Config);
    }

    @Override
    public void IMUAccelUpdate(float ax, float ay, float az) {
        if (this.ax != null){
            this.ax.setText("" + df.format(ax));
        }
        if (this.ay != null){
            this.ay.setText("" + df.format(ay));
        }
        if (this.az != null){
            this.az.setText("" + df.format(az));
        }
    }

    @Override
    public void IMUGyroUpdate(float gx, float gy, float gz) {
        if (this.gx != null){
            this.gx.setText("" + df.format(gx));
        }
        if (this.gy != null){
            this.gy.setText("" + df.format(gy));
        }
        if (this.gz != null){
            this.gz.setText("" + df.format(gz));
        }
    }

    @Override
    public void dataUpdated(float frequency, float response) {
        if (!dataForSeries.isEmpty() && frequency < dataForSeries.get(dataForSeries.size() - 1).getX()){
            dataForSeries.clear();
        }
        dataForSeries.add(new DataPoint(frequency, response));
        DataPoint[] dataPoints = new DataPoint[dataForSeries.size()];
        dataPoints = dataForSeries.toArray(dataPoints);
        frequencySeries.resetData(dataPoints);
        /*
        Log.d("AD5932", "Data Update freq " + frequency + " resp " + response);
        DataPoint dataPoint = new DataPoint(frequency, response);
        frequencySeries.appendData(dataPoint, false, 200, false);
         */
    }

    @Override
    public void EndOfScan() {
        frequencySeries.resetData(null);
    }
/*
    @Override
    public void changeConfig(AD5932Config conf) {
        if (this.bluetoothClientManager != null && conf != null){
            bluetoothClientManager.write(conf.packConfigForTransfer());
        }
    }
    */
}