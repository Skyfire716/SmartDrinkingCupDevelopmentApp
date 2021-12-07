package com.jonas.weigand.thesis.smartdrinkingcup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AD5932Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AD5932Fragment extends Fragment implements View.OnClickListener {

    protected Button collapseSettings;
    protected AD5932SettingsFragment ad5932SettingsFragment;
    protected FragmentContainerView fragmentContainerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        collapseSettings = (Button) getView().findViewById(R.id.collapseSettings);
        collapseSettings.setOnClickListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ad5932SettingsFragment = new AD5932SettingsFragment();
        transaction.replace(R.id.ad5932settings, ad5932SettingsFragment);
        transaction.commit();
     }

    @Override
    public void onClick(View v) {
        if(v == collapseSettings){
            if (ad5932SettingsFragment.getView().getVisibility() == View.VISIBLE){
                ad5932SettingsFragment.getView().setVisibility(View.INVISIBLE);
                collapseSettings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_keyboard_arrow_right_24, 0, 0, 0);
            }else {
                ad5932SettingsFragment.getView().setVisibility(View.VISIBLE);
                collapseSettings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_keyboard_arrow_down_24, 0, 0, 0);
            }
        }
    }
}