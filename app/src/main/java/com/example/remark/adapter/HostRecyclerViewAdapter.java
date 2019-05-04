package com.example.remark.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.remark.R;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.PreventDoubleClickOnClickListener;
import com.example.remark.ui.lobby.JoinFragment;
import com.example.remark.ui.lobby.WaitingFragment;

public class HostRecyclerViewAdapter extends RecyclerView.Adapter<HostRecyclerViewAdapter.ViewHolder> {
    private JoinFragment mFragment;

    public HostRecyclerViewAdapter(Fragment fragment) {
        this.mFragment = (JoinFragment)fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_host, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ViewData bluetooth = mFragment.getBluetooths().get(position);

        holder.hostNameTextView.setText(bluetooth.name);
        holder.hostNameTextView.setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View view) {
                //attempt to Connect to Server
                Bluetooth.getInstance().newBluetoothClient(bluetooth.address);

                mFragment.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main_vg_fragment, WaitingFragment.newInstance(false))
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFragment.getBluetooths().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView hostNameTextView;

        public ViewHolder(View view) {
            super(view);

            hostNameTextView = (TextView)view.findViewById(R.id.hostNameTextView);
        }
    }

    public class ViewData {
        public String name;
        public String address;

        public ViewData(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }
}
