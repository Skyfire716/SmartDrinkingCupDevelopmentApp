package com.jonas.weigand.thesis.smartdrinkingcup;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AD5932SettingsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, Switch.OnCheckedChangeListener {

    protected Switch b24_sw;
    protected Switch dac_enable_sw;
    protected Switch sine_sw;
    protected Switch msbout_sw;
    protected Switch intinc_sw;
    protected Switch sync_sel_sw;
    protected Switch syncout_sw;
    protected Switch inc_by_sw;

    private int start_frequency_multiplier;
    private int delta_frequency_multiplier;

    protected Spinner multiplier_spinner;
    protected Spinner start_frequency_prefix_spinner;
    protected Spinner delta_frequency_prefix_spinner;

    protected EditText start_frequency_edit_text;
    protected EditText delta_frequency_edit_text;
    protected EditText number_inc_edit_text;
    protected EditText inc_interval_edit_text;

    private TextWatcher number_increments_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0) {
                return;
            }
            int value = 0;
            if (editable != null) {
                try {
                    value = Integer.parseInt(editable.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (value < 2 || value > 4095) {
                    if (value < 2) {
                        number_inc_edit_text.setText("2");
                    } else {
                        number_inc_edit_text.setText("4095");
                    }
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" must be >= 2 or <= 4095")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                } else {
                    ad5932Config.setNumber_of_increments(value);
                    apply_btn.setClickable(true);
                }
            }
        }
    };
    private TextWatcher start_frequency_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0) {
                return;
            }
            double value = 0;
            boolean error_occured = false;
            if (editable != null) {
                try {
                    String text = editable.toString().replace(",", ".");
                    if (text.replaceAll("0|1|2|3|4|5|6|7|8|9", "").length() > 1) {
                        error_occured = true;
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + text + "\" contains multiple \".\", \",\" or both")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else {
                        value = Double.parseDouble(text);
                    }
                } catch (NumberFormatException e) {
                    error_occured = true;
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" could not be parsed as a number")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    e.printStackTrace();
                }
                if (!error_occured) {
                    value *= start_frequency_multiplier;
                    if (value < 0 || value > 16777215) {
                        if (value < 0) {
                            start_frequency_edit_text.setText("0");
                        } else {
                            start_frequency_edit_text.setText("" + (16777215.0 / start_frequency_multiplier));
                        }
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" must be >= 0 or <= 16777215")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else if ((start_frequency_multiplier == 1 && (value - ((int) value)) != 0.0) || (start_frequency_multiplier == 1000 && (value * 1000 - ((int) value * 1000) != 0.0)) || (start_frequency_multiplier == 1000000 && (value * 1000000 - ((int) value * 1000000) != 0.0))) {
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" * \"" + start_frequency_multiplier + "\" a frequency must be a whole number")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else {
                        ad5932Config.setStart_frequency((int) value);
                        apply_btn.setClickable(true);
                    }
                }
            }
        }
    };
    private TextWatcher delta_frequency_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0) {
                return;
            }
            double value = 0;
            boolean error_occured = false;
            if (editable != null) {
                try {
                    String text = editable.toString().replace(",", ".");
                    if (text.replaceAll("-|0|1|2|3|4|5|6|7|8|9", "").length() > 1) {
                        error_occured = true;
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + text + "\" contains multiple \".\", \",\" or both")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else {
                        value = Double.parseDouble(text);
                    }
                } catch (NumberFormatException e) {
                    error_occured = true;
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" could not be parsed as a number")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    e.printStackTrace();
                }
                if (!error_occured) {
                    value *= delta_frequency_multiplier;
                    if (value < -8388607.0 || value > 8388607) {
                        if (value < -8388607.0) {
                            start_frequency_edit_text.setText("8388607.0");
                        } else {
                            start_frequency_edit_text.setText("" + (8388607.0 / start_frequency_multiplier));
                        }
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" must be >= 8388607.0 or <= 8388607.0")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else if ((delta_frequency_multiplier == 1 && (value - ((int) value)) != 0.0) || (delta_frequency_multiplier == 1000 && (value * 1000 - ((int) value * 1000) != 0.0)) || (delta_frequency_multiplier == 1000000 && (value * 1000000 - ((int) value * 1000000) != 0.0))) {
                        new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" * \"" + start_frequency_multiplier + "\" a frequency must be a whole number")
                                .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                    } else {
                        ad5932Config.setDelta_frequency((int) value);
                        apply_btn.setClickable(true);
                    }
                }
            }
        }
    };
    private TextWatcher increment_interval_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0) {
                return;
            }
            int value = 0;
            if (editable != null) {
                try {
                    value = Integer.parseInt(editable.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (value < 2 || value > 2047) {
                    if (value < 2) {
                        inc_interval_edit_text.setText("2");
                    } else {
                        inc_interval_edit_text.setText("2047");
                    }
                    new AlertDialog.Builder(getActivity()).setTitle("Invalid Input").setMessage("Your Input \"" + editable.toString() + "\" must be >= 2 or <= 2047")
                            .setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show();
                } else {
                    ad5932Config.setIncrement_interval(value);
                    apply_btn.setClickable(true);
                }
            }
        }
    };

    protected Button apply_btn;

    private AD5932Config ad5932Config;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AD5932SettingsFragment() {
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
    public static AD5932SettingsFragment newInstance(String param1, String param2) {
        AD5932SettingsFragment fragment = new AD5932SettingsFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_d5932_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b24_sw = (Switch) view.findViewById(R.id.b24_sw);
        dac_enable_sw = (Switch) view.findViewById(R.id.dac_en_sw);
        sine_sw = (Switch) view.findViewById(R.id.sine_sw);
        msbout_sw = (Switch) view.findViewById(R.id.msbout_sw);
        intinc_sw = (Switch) view.findViewById(R.id.int_inc_sw);
        sync_sel_sw = (Switch) view.findViewById(R.id.synsel_sw);
        syncout_sw = (Switch) view.findViewById(R.id.syncout_sw);
        inc_by_sw = (Switch) view.findViewById(R.id.period_switch);

        b24_sw.setOnCheckedChangeListener(this);
        dac_enable_sw.setOnCheckedChangeListener(this);
        sine_sw.setOnCheckedChangeListener(this);
        msbout_sw.setOnCheckedChangeListener(this);
        intinc_sw.setOnCheckedChangeListener(this);
        sync_sel_sw.setOnCheckedChangeListener(this);
        syncout_sw.setOnCheckedChangeListener(this);
        inc_by_sw.setOnCheckedChangeListener(this);


        multiplier_spinner = (Spinner) view.findViewById(R.id.multiplier_spinner);
        start_frequency_prefix_spinner = (Spinner) view.findViewById(R.id.start_frequency_prefix_spinner);
        delta_frequency_prefix_spinner = (Spinner) view.findViewById(R.id.delta_frequency_prefix_spinner);

        multiplier_spinner.setOnItemSelectedListener(this);
        start_frequency_prefix_spinner.setOnItemSelectedListener(this);
        delta_frequency_prefix_spinner.setOnItemSelectedListener(this);

        start_frequency_edit_text = (EditText) view.findViewById(R.id.start_frequency_edit_text);
        delta_frequency_edit_text = (EditText) view.findViewById(R.id.delta_frequency_text_edit);
        number_inc_edit_text = (EditText) view.findViewById(R.id.number_inc_edit_text);
        inc_interval_edit_text = (EditText) view.findViewById(R.id.duration_edit_text);

        start_frequency_edit_text.addTextChangedListener(start_frequency_TextWatcher);
        delta_frequency_edit_text.addTextChangedListener(delta_frequency_TextWatcher);
        number_inc_edit_text.addTextChangedListener(number_increments_TextWatcher);
        inc_interval_edit_text.addTextChangedListener(increment_interval_TextWatcher);

        apply_btn = (Button) view.findViewById(R.id.applyBtn);
        apply_btn.setOnClickListener(this);
        ad5932Config = new AD5932Config();
    }

    @Override
    public void onClick(View view) {
        if (view == apply_btn) {
            Log.d("AD5932Settings", "Apply " + ad5932Config.toString());
            //ad5932Config.printRegister(ad5932Config.getControl());
            //ad5932Config.printRegister(ad5932Config.getNumberOnIncrements());
            //ad5932Config.printRegister(ad5932Config.getLowerDeltaFrequency());
            //ad5932Config.printRegister(ad5932Config.getUpperDeltaFrequency());
            //ad5932Config.printRegister(ad5932Config.getIncrementIntervalReg());
            //ad5932Config.printRegister(ad5932Config.getLowerStartFrequency());
            //ad5932Config.printRegister(ad5932Config.getUpperStartFrequency());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == multiplier_spinner) {
            String multiplier = (String) adapterView.getItemAtPosition(i);
            switch (multiplier) {
                case "x1":
                    ad5932Config.setMultiplier((byte) 0);
                    break;
                case "x5":
                    ad5932Config.setMultiplier((byte) 1);
                    break;
                case "x100":
                    ad5932Config.setMultiplier((byte) 2);
                    break;
                case "x500":
                    ad5932Config.setMultiplier((byte) 3);
                    break;
                default:
                    return;
            }
            apply_btn.setClickable(true);
        } else {
            int prefix_multiplier = 0;
            String prefix = (String) adapterView.getItemAtPosition(i);
            if (prefix.contains("MHz")) {
                prefix_multiplier = 1000000;
            } else if (prefix.contains("kHz")) {
                prefix_multiplier = 1000;
            } else {
                prefix_multiplier = 1;
            }
            if (adapterView == start_frequency_prefix_spinner) {
                start_frequency_multiplier = prefix_multiplier;
                start_frequency_TextWatcher.afterTextChanged(start_frequency_edit_text.getText());
            } else if (adapterView == delta_frequency_prefix_spinner) {
                delta_frequency_multiplier = prefix_multiplier;
                delta_frequency_TextWatcher.afterTextChanged(delta_frequency_edit_text.getText());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == b24_sw) {
            ad5932Config.setB24(b);
        } else if (compoundButton == dac_enable_sw) {
            ad5932Config.setDac_enable(b);
        } else if (compoundButton == sine_sw) {
            ad5932Config.setSine(b);
        } else if (compoundButton == msbout_sw) {
            ad5932Config.setMsbout(b);
        } else if (compoundButton == intinc_sw) {
            ad5932Config.setInt_inc(b);
        } else if (compoundButton == sync_sel_sw) {
            ad5932Config.setSync_sel(b);
        } else if (compoundButton == syncout_sw) {
            ad5932Config.setSync_out(b);
        } else if (compoundButton == inc_by_sw) {
            ad5932Config.setInc_on_cycles(b);
        } else {
            return;
        }
        apply_btn.setClickable(true);
    }
}