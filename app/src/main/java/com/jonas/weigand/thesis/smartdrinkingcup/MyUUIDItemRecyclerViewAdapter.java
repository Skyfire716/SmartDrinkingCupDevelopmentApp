package com.jonas.weigand.thesis.smartdrinkingcup;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jonas.weigand.thesis.smartdrinkingcup.databinding.FragmentItem2Binding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.jonas.weigand.thesis.smartdrinkingcup.UUIDItemContent.UUIDItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUUIDItemRecyclerViewAdapter extends RecyclerView.Adapter<MyUUIDItemRecyclerViewAdapter.ViewHolder> {

    private final List<UUIDItemContent.UUIDItem> mValues;

    public MyUUIDItemRecyclerViewAdapter(List<UUIDItemContent.UUIDItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItem2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).uuid);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public UUIDItemContent.UUIDItem mItem;

        public ViewHolder(FragmentItem2Binding binding) {
            super(binding.getRoot());
            mIdView = binding.uuid;
            mContentView = binding.uuidcontent;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}