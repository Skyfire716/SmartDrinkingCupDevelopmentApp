package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jonas.weigand.thesis.smartdrinkingcup.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BluetoothDeviceItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BluetoothDevicesRecyclerViewAdapter extends RecyclerView.Adapter<BluetoothDevicesRecyclerViewAdapter.ViewHolder> {

    private final List<BluetoothDeviceItem> mValues;
    private  final IDeviceHandler deviceHandler;

    public BluetoothDevicesRecyclerViewAdapter(List<BluetoothDeviceItem> items, IDeviceHandler handler) {
        mValues = items;
        this.deviceHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBluetoothDeviceItem = mValues.get(position);
        holder.deviceHandler = deviceHandler;
        holder.mDeviceNameView.setText(mValues.get(position).deviceName);
        holder.mDeviceMACView.setText(mValues.get(position).deviceMAC);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public final TextView mDeviceNameView;
        public final TextView mDeviceMACView;
        public BluetoothDeviceItem mBluetoothDeviceItem;
        private IDeviceHandler deviceHandler;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mDeviceNameView = binding.deviceName;
            mDeviceMACView = binding.deviceMac;
            mDeviceNameView.setOnClickListener(this);
            mDeviceMACView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDeviceMACView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (v == mDeviceMACView || v == mDeviceNameView){
                Log.d(String.valueOf(R.string.ui_interaction), "Clicked on Device " + mDeviceNameView.getText() + " with MAC " + mDeviceMACView.getText());
                if (deviceHandler == null){
                    Log.e("Device Handler is NULL", "Device Handler is NULL");
                }else {
                    deviceHandler.connectToDevice(mBluetoothDeviceItem.device);
                }
            }
        }
    }
}